package display;

import com.jgraph.layout.JGraphCompoundLayout;
import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.JGraphLayout;
import com.jgraph.layout.graph.JGraphISOMLayout;
import com.jgraph.layout.graph.JGraphSimpleLayout;
import com.jgraph.layout.hierarchical.JGraphHierarchicalLayout;
import com.jgraph.layout.organic.JGraphFastOrganicLayout;
import com.jgraph.layout.organic.JGraphSelfOrganizingOrganicLayout;
import com.jgraph.layout.tree.JGraphCompactTreeLayout;
import com.jgraph.layout.tree.JGraphMoenLayout;
import com.jgraph.layout.tree.JGraphRadialTreeLayout;
import com.jgraph.layout.tree.JGraphTreeLayout;
import model.Implicant;
import model.Varieable;
import org.jgraph.*;
import org.jgraph.graph.*;
import reader.FileScanner;
import util.Combination;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * Created by guichi on 10/05/2015.
 */
public class GraphPanel extends JFrame implements Observer {

    //utility
    private final Random generator = new Random();
    private FileScanner fileScanner;
    private Observable observable;//listen to input file
    String fileName = "raw.txt";//initial input file


    //data structure
    private List<GraphCell> cells;//all the cells including node and edge
    private Map<String, Integer> register;//{variableName-->count}
    private List<Implicant> processedImplicantList;//[positive_variable_in_a_implicant]
    private List<Implicant> multipleVariableImplicantList;
    private Map<String, List<DefaultGraphCell>> groupMap;//{variableName->variableCell}
    private Map<String, List<List<Varieable>>> variabelCombinationPair;
    private List<String> combinationVariables;
    private int maxNumOfV = 0;


    //GUI
    private JScrollPane scrollPane;
    JFileChooser fc = new JFileChooser();
    JGraph graph;
    GraphModel model;
    FilePublisher filePublisher;
    //size of the JGraph pane
    int width;
    int height;
    //size of individual cell
    int minWidth = 50;
    int minHeight = 30;
    int widthExpension = 100;
    int heightExpension = 20;
    //thickness of cell border
    int minThickness = 1;
    float thicknessExpension = 5;


    //properties
    boolean displayLink;
    Properties properties;


    public GraphPanel() {
        start();
    }


    public void readProperties() {
        String propertyFileName = "config.properties";
        properties = new Properties();
        File initialFile = new File(propertyFileName);
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(initialFile);
            properties.load(inputStream);
            displayLink = Boolean.parseBoolean(properties.getProperty("showLinkBetweenImplicants"));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void initialize() {
        setTitle(fileName);
        //initialization
        processedImplicantList = new ArrayList<>();
        multipleVariableImplicantList = new ArrayList<>();
        variabelCombinationPair = new HashMap<>();
        combinationVariables = new ArrayList<>();
        groupMap = new HashMap<>();
        filePublisher = new FilePublisher();
        register = new HashMap<>();
        observable = filePublisher;
        observable.addObserver(this);
        cells = new ArrayList();
        model = new DefaultGraphModel();
        GraphLayoutCache view = new GraphLayoutCache(model, new DefaultCellViewFactory());
        graph = new JGraph(model, view);

        try {
            fileScanner = new FileScanner(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        //fill in register and processedList
        while (fileScanner.hasNext()) {
            Implicant implicant = FileScanner.readLine(fileScanner.next());
            Implicant processedImplicant = new Implicant();
            while (implicant.hasNext()) {
                Varieable varieable = implicant.next();
                if (varieable.isPresent()) {
                    String variableName = varieable.getName();
                    int variableCount;

                    if (register.get(variableName) == null) {
                        variableCount = 1;
                        register.put(variableName, variableCount);
                    } else {
                        int count = register.get(variableName);
                        variableCount = ++count;
                        register.put(variableName, variableCount);
                    }
                    if (variableCount > maxNumOfV) {
                        maxNumOfV = variableCount;
                    }

                    varieable.setNum(variableCount);
                    processedImplicant.add(varieable);
                }
            }
            if (!processedImplicant.isAllNegetive()) {
                processedImplicantList.add(processedImplicant);
            }

        }
        //System.out.println("\nregister :\n" + register);
//        System.out.println("maxNumOfV is : " + maxNumOfV);
//        System.out.println("\nprocessedList :\n" + processedImplicantList);

        //determine the size of the pane
        int numberOfNode = 0;

        //fill in multipleVariableImplicantList
        for (Implicant implicant : processedImplicantList) {
            if (!implicant.isSingle()) {
                multipleVariableImplicantList.add(implicant);
            }
            numberOfNode += implicant.getSize();

        }

        //System.out.println(numberOfNode);
        height = numberOfNode * 10;
        width = numberOfNode * 12;


//        System.out.println("\nmultipleVariableList :\n" + multipleVariableImplicantList);


    }


    public void drawGraph() {
//        System.out.println("Start drawgraph");
//        System.out.println(cells);
        cells.clear();
//        System.out.println("Clear");
//        System.out.println(cells);
        //add nodes and the edges between them
        for (Implicant implicant : processedImplicantList) {
            //cells in the same implicant
            List<DefaultGraphCell> varieableCells = new ArrayList();
            //edges between cells in same implicant
            List<DefaultEdge> edgeList = new ArrayList<>();
            //add cells
            while (implicant.hasNext()) {
                Varieable varieable = implicant.next();


                DefaultPort port = new DefaultPort();
                DefaultPort[] ports = new DefaultPort[1];
                ports[0] = port;
                DefaultGraphCell cell = new DefaultGraphCell(varieable, null, ports);
                int presentTime = register.get(varieable.getName());

                //decide border thickness
                float thicknessResolution = thicknessExpension / maxNumOfV;
                GraphConstants.setBorder(cell.getAttributes(), new LineBorder(Color.black,
                        (int) (minThickness + presentTime * thicknessResolution), true));
                // decide color
                if (presentTime == 1) {
                    GraphConstants.setGradientColor(cell.getAttributes(), Color.yellow);
                } else if (240 - presentTime * 20 > 0) {

                    GraphConstants.setGradientColor(cell.getAttributes(), new Color(240, 0 + 20 * presentTime, 240));
                } else {
                    GraphConstants.setGradientColor(cell.getAttributes(),
                            new Color(240 - 1 * presentTime, 0 + 1 * presentTime, 240 - 1 * presentTime));
                }


                GraphConstants.setOpaque(cell.getAttributes(), true);
                //decide size and position
                int heightResolution = heightExpension / maxNumOfV;
                int widthResolution = widthExpension / maxNumOfV;
                int initX = generator.nextInt(width);
                int initY = generator.nextInt(height);

                GraphConstants.setBounds(cell.getAttributes(),
                        new Rectangle2D.Double(initX, initY,
                                minWidth + widthResolution * presentTime, minHeight + heightResolution * presentTime));
                varieableCells.add(cell);


                //fill in group map
                if (register.get(varieable.getName()) >= 2) {
                    //System.out.println(groupMap.containsKey(varieable.getName()));
                    List<DefaultGraphCell> groupList = groupMap.get(varieable.getName());
                    if (groupList == null) {
                        groupList = new ArrayList<>();
                        groupList.add(cell);
                        groupMap.put(varieable.getName(), groupList);
                    } else {
                        groupList.add(cell);
                    }
                }
            }
            cells.addAll(varieableCells);

            //edges between the nodes from same implicant
            if (varieableCells.size() > 1)
                for (int j = 0; j < varieableCells.size(); j++) {
                    DefaultEdge edge = new DefaultEdge();
                    GraphConstants.setLineWidth(edge.getAttributes(), 5);
                    GraphConstants.setLineColor(edge.getAttributes(), Color.blue);
                    if (j + 1 < varieableCells.size()) {
                        DefaultPort source = (DefaultPort) varieableCells.get(j).getChildAt(0);
                        DefaultPort target = (DefaultPort) varieableCells.get(j + 1).getChildAt(0);
                        edge.setSource(source);
                        edge.setTarget(target);
                        edgeList.add(edge);
                    }

                }
            cells.addAll(edgeList);
        }


        if (displayLink) {
            //edge between nodes with same name in different implicants
            for (String k : groupMap.keySet()) {
                List<DefaultGraphCell> l = groupMap.get(k);
                for (int i = 0; i < l.size(); i++) {
                    DefaultEdge edge = new DefaultEdge();
                    float[] pattern = {5, 5};
                    GraphConstants.setDashPattern(edge.getAttributes(), pattern);
                    GraphConstants.setLineColor(edge.getAttributes(), Color.MAGENTA);
                    GraphConstants.setLineWidth(edge.getAttributes(), 3);
                    if (i + 1 < l.size()) {
                        DefaultPort source = (DefaultPort) l.get(i).getChildAt(0);
                        DefaultPort target = (DefaultPort) l.get(i + 1).getChildAt(0);
                        edge.setSource(source);
                        edge.setTarget(target);
                        cells.add(edge);
                    }
                }
            }

//            System.out.println("re-draw");
//            System.out.println(cells);
        }

    }

    public void renderCells() {
        graph.getGraphLayoutCache().insert(cells.toArray());

        String layoutOption = properties.getProperty("layout");
        JGraphFacade facade = new JGraphFacade(graph);
        JGraphLayout layout = null;
        switch (layoutOption) {
            case "1":
                layout = new JGraphHierarchicalLayout();
                break;
//        JGraphLayout layout = new JGraphRadialTreeLayout();
            case "2":
                layout = new JGraphCompactTreeLayout();
                break;
//        JGraphLayout layout=new JGraphMoenLayout();
//        JGraphLayout layout=new JGraphFastOrganicLayout();
//        JGraphLayout layout=new JGraphTreeLayout();
//        JGraphLayout layout=new JGraphCompoundLayout();
//        JGraphLayout layout = new JGraphSelfOrganizingOrganicLayout();
//        JGraphLayout layout=new JGraphISOMLayout();
        }
        layout.run(facade);
        Map nested = facade.createNestedMap(true, true);
        //graph.setPreferredSize(new Dimension(500,500));
        graph.setAutoscrolls(true);
        graph.getGraphLayoutCache().edit(nested);
        scrollPane = new JScrollPane(graph,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(Integer.parseInt(properties.getProperty("scrollPaneWidth")),
                Integer.parseInt(properties.getProperty("scrollPaneLength"))));
        add(scrollPane, BorderLayout.NORTH);
    }


    public void rerenderCells() {
        remove(scrollPane);
        graph.removeAll();
        renderCells();
        validate();
    }

    public void start() {

        {
            readProperties();
            initialize();
            drawGraph();
            if (fileName.equals("raw.txt")) {
                renderCells();
            } else {
                rerenderCells();
            }


            JPanel controlPanel = new JPanel();
            controlPanel.setOpaque(true);
            controlPanel.setBounds(0, 0, 200, 200);

            JButton showCombineButton = new JButton("show combination");

            JButton hideSingleButton = new JButton("hide single");
            hideSingleButton.addActionListener(new HideSingleListener());
            controlPanel.add(hideSingleButton);


            JButton addButton = new JButton("Select a file");
            addButton.addActionListener(filePublisher);
            controlPanel.add(addButton);


            JButton restoreButton = new JButton("restore");
            restoreButton.addActionListener(
                    e -> {
                        initialize();
                        drawGraph();
                        rerenderCells();
                    }
            );

            JButton combinationButton = new JButton("Show combination");
            combinationButton.addActionListener(new ShowCombinedListener());


            JButton addLinkButton=new JButton("Add link");
            addLinkButton.addActionListener(new AddlinkListener());
            controlPanel.add(restoreButton);
            controlPanel.add(combinationButton);
            controlPanel.add(addLinkButton);


            add(controlPanel, BorderLayout.EAST);
        }


    }


    //inner class to notify publish information when user pass in a new input file
    class FilePublisher extends Observable implements ActionListener {
        String tmpfileName;

        public String getFileName() {
            return tmpfileName;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            fc.showOpenDialog(GraphPanel.this);

            File file = fc.getSelectedFile();
            tmpfileName = file.toString();
            setChanged();
            notifyObservers();


        }
    }

    //listener for combined button
    class ShowCombinedListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            for (Implicant implicant : multipleVariableImplicantList) {
                System.out.println("Implicants : \n " + implicant);
                List<Varieable> vList = implicant.getVarieables();
                List<List<Varieable>> combinationList = Combination.combination(vList.toArray(), 2);
                for (List<Varieable> combination : combinationList) {
                    System.out.println(combination);
                    for (Varieable v : combination) {
                        System.out.println("s :" + v);
                        String name = v.getName();
                        if (!variabelCombinationPair.containsKey(name)) {
                            variabelCombinationPair.put(name, new ArrayList<>());

                        }
                        List<List<Varieable>> variableValue = variabelCombinationPair.get(name);
                        variableValue.add(combination);

                    }
                }
            }
            System.out.println("variabelCombinationPair" + variabelCombinationPair);
            for (List<List<Varieable>> v : variabelCombinationPair.values()) {
                int len = v.size();
                for (int i = 0; i < len; i++) {
                    List<Varieable> combin1 = v.get(i);
                    Collections.sort(combin1);
                    for (int j = i + 1; j < len; j++) {
                        List<Varieable> combin2 = v.get(j);
                        Collections.sort(combin2);

                        if (combin1.equals(combin2)) {
                            System.out.println(combin1 + " and " + combin2 + " are equal");
                            for (Varieable v1 : combin1) {
                                combinationVariables.add(v1.toString());
                            }
                            for (Varieable v2 : combin2) {
                                combinationVariables.add(v2.toString());
                            }

                        }
                    }
                }

            }
            System.out.println("combinationVariables : " + combinationVariables);

            for (GraphCell cell : cells) {
                String name = "";
                Object userObject = model.getValue(cell);
                if (userObject != null) {
                    name = userObject.toString();
                    if (!combinationVariables.contains(name)) {
                        System.out.println(name + " is in combinationVariables");
                        GraphConstants.setOpaque(cell.getAttributes(), false);
                        GraphConstants.setValue(cell.getAttributes(), "");
                        GraphConstants.setBorder(cell.getAttributes(), BorderFactory.createEmptyBorder());
                    }
                }
            }

            System.out.println("All the cells : " + cells);
            hideEdges();
            rerenderCells();


        }
    }

    //listen to hide single button
    class HideSingleListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Hide the single ");
            //System.out.println(multipleVariableImplicantList);
            List<String> nameList = new ArrayList<>();
            for (Implicant implicant : multipleVariableImplicantList) {
                for (Varieable varieable : implicant.getVarieables()) {
                    nameList.add(varieable.toString());
                }

            }
            //System.out.println("namelist" + nameList);
            for (GraphCell cell : cells) {
                String name = "";
                Object userObject = model.getValue(cell);
                if (userObject != null) {
                    name = userObject.toString();
                    if (!nameList.contains(name)) {
                        //System.out.println(name + " is in list");
                        GraphConstants.setOpaque(cell.getAttributes(), false);
                        GraphConstants.setValue(cell.getAttributes(), "");
                        GraphConstants.setBorder(cell.getAttributes(), BorderFactory.createEmptyBorder());
                    }
                }
            }
            System.out.println("All the cells : " + cells);

            hideEdges();
            rerenderCells();

        }
    }

    class AddlinkListener implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e) {
            for (String k : groupMap.keySet()) {
                List<DefaultGraphCell> l = groupMap.get(k);
                for (int i = 0; i < l.size(); i++) {
                    DefaultEdge edge = new DefaultEdge();
                    float[] pattern = {5, 5};
                    GraphConstants.setDashPattern(edge.getAttributes(), pattern);
                    GraphConstants.setLineColor(edge.getAttributes(), Color.MAGENTA);
                    GraphConstants.setLineWidth(edge.getAttributes(), 3);
                    if (i + 1 < l.size()) {
                        DefaultPort source = (DefaultPort) l.get(i).getChildAt(0);
                        DefaultPort target = (DefaultPort) l.get(i + 1).getChildAt(0);
                        edge.setSource(source);
                        edge.setTarget(target);
                        cells.add(edge);
                    }
                }
            }
            renderCells();
            validate();

        }
    }


    @Override
    public void update(Observable o, Object arg) {
        FilePublisher filePublisher = (FilePublisher) o;
        fileName = filePublisher.getFileName();
        cells = new ArrayList<>();
        start();
    }


    public void hideEdges() {
        for (GraphCell cell : cells) {
            System.out.print("The cell is " + cell);

            if (cell instanceof DefaultEdge) {
                //System.out.print("\tThis is a Edge ");
                DefaultEdge edgeCell = (DefaultEdge) cell;
                DefaultGraphCell s = (DefaultGraphCell) ((DefaultPort) edgeCell.getSource()).getParent();
                DefaultGraphCell t = (DefaultGraphCell) ((DefaultPort) edgeCell.getTarget()).getParent();
                //System.out.println("between "+s+" and "+t);
                Map sAttributes = s.getAttributes();
                Map tAttributes = t.getAttributes();
                //System.out.println("Source "+sAttributes.get("opaque"));
                //System.out.println(!(boolean) sAttributes.get("opaque") || !(boolean) tAttributes.get("opaque"));
                if (!(boolean) sAttributes.get("opaque") || !(boolean) tAttributes.get("opaque")) {
                    //  System.out.println("Hide the edge");
                    GraphConstants.setOpaque(cell.getAttributes(), false);
                    GraphConstants.setGradientColor(cell.getAttributes(), Color.white);
                }
            }

        }

    }


}
