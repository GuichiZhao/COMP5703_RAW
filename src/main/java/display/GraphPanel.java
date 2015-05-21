package display;

import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.JGraphLayout;
import com.jgraph.layout.hierarchical.JGraphHierarchicalLayout;
import com.jgraph.layout.tree.JGraphTreeLayout;
import model.Implicant;
import model.Varieable;
import org.jgraph.*;
import org.jgraph.graph.*;

import reader.FileScanner;

import javax.swing.*;
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
    String fileName="raw.txt";//initial input file



    //data structure
    private List<GraphCell> cells;//all the cells including node and edge
    private Map<String, Integer> register;//{variableName-->count}
    private List<Implicant> processedImplicantList;//[positive_variable_in_a_implicant]
    private Map<String, List<DefaultGraphCell>> groupMap;//{variableName->variableCell}


    //GUI
    private JScrollPane scrollPane;
    JFileChooser fc = new JFileChooser();
    JGraph graph;


    public GraphPanel()
    {
        start();
    }

    public void start() {


        {
            //System.out.println(fileName+" will be used \n");
            processedImplicantList = new ArrayList<>();
            groupMap = new HashMap<>();
            FilePublisher filePublisher = new FilePublisher();
            register=new HashMap<>();
            observable = filePublisher;
            observable.addObserver(this);


            cells = new ArrayList();
            try {
                fileScanner = new FileScanner(fileName);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            GraphModel model = new DefaultGraphModel();
            GraphLayoutCache view = new GraphLayoutCache(model, new DefaultCellViewFactory());
             graph = new JGraph(model, view);

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

                        varieable.setNum(variableCount);
                        //System.out.println(varieable);
                        processedImplicant.add(varieable);


                    }
                }

                //System.out.println(processedImplicant);


                if (!processedImplicant.isAllNegetive()) {
                    processedImplicantList.add(processedImplicant);
                }


            }

            for (Implicant implicant : processedImplicantList) {
                List<DefaultGraphCell> varieableCells = new ArrayList();
                List<DefaultEdge> edgeList = new ArrayList<>();
                while (implicant.hasNext()) {
                    Varieable varieable = implicant.next();


                    DefaultPort port = new DefaultPort();
                    DefaultPort[] ports = new DefaultPort[1];
                    ports[0] = port;
                    DefaultGraphCell cell = new DefaultGraphCell(varieable, null, ports);
                    if (register.get(varieable.getName()) == 2) {
                        GraphConstants.setGradientColor(cell.getAttributes(), Color.RED);
                    } else if (register.get(varieable.getName()) == 3) {
                        GraphConstants.setGradientColor(cell.getAttributes(), Color.CYAN);
                    } else {

                        GraphConstants.setGradientColor(cell.getAttributes(), Color.BLUE);
                    }
                    GraphConstants.setOpaque(cell.getAttributes(), true);
                    GraphConstants.setBounds(cell.getAttributes(),
                            new Rectangle2D.Double(generator.nextInt(1), generator.nextInt(1), 80, 20));
                    varieableCells.add(cell);


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


                      //System.out.println(varieable);
                }
                //System.out.println(varieableCells);
                cells.addAll(varieableCells);

                if (varieableCells.size() > 1)
                    for (int j = 0; j < varieableCells.size(); j++) {
                        DefaultEdge edge = new DefaultEdge();
                        GraphConstants.setLineWidth(edge.getAttributes(), 5);
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

            for (String k : groupMap.keySet()) {
                List<DefaultGraphCell> l = groupMap.get(k);
                for (int i = 0; i < l.size(); i++) {
                    DefaultEdge edge = new DefaultEdge();
                    GraphConstants.setLineColor(edge.getAttributes(), Color.MAGENTA);
                    GraphConstants.setLineWidth(edge.getAttributes(), 12);
                    if (i + 1 < l.size()) {
                        DefaultPort source = (DefaultPort) l.get(i).getChildAt(0);
                        DefaultPort target = (DefaultPort) l.get(i + 1).getChildAt(0);
                        edge.setSource(source);
                        edge.setTarget(target);
                        cells.add(edge);
                    }
                }
            }


            System.out.println(cells);


            if(fileName.equals("raw.txt")) {
                graph.getGraphLayoutCache().insert(cells.toArray());
                JGraphFacade facade = new JGraphFacade(graph);
                JGraphLayout layout = new JGraphHierarchicalLayout() {
                };
                layout.run(facade);
                Map nested = facade.createNestedMap(true, true);
                graph.getGraphLayoutCache().edit(nested);
                scrollPane=new JScrollPane(graph);
                add(scrollPane, BorderLayout.NORTH);
            }
            else
            {

                System.out.println("Display "+fileName);
                remove(scrollPane);
                graph.removeAll();
                graph.getGraphLayoutCache().insert(cells.toArray());
                JGraphFacade facade = new JGraphFacade(graph);
                JGraphLayout layout = new JGraphHierarchicalLayout() {
                };
                layout.run(facade);
                Map nested = facade.createNestedMap(true, true);
                graph.getGraphLayoutCache().edit(nested);
                graph.refresh();
                graph.validate();

                scrollPane=new JScrollPane(graph);
                add(scrollPane, BorderLayout.NORTH);
                repaint();
                validate();

            }


            JPanel controlPanel = new JPanel();
            controlPanel.setOpaque(true);
            controlPanel.setBounds(0, 0, 200, 200);
            JButton addButton = new JButton("Select a file");
            addButton.addActionListener(filePublisher);
            controlPanel.add(addButton);


            JButton layoutButton=new JButton("Change layout");
            //lambda expression
            layoutButton.addActionListener(a->{
                JGraphFacade facade = new JGraphFacade(graph);
                int i=generator.nextInt(2);
                JGraphLayout layout;
                if (i==1) {
                    layout = new JGraphTreeLayout();
                }
                else
                {
                    layout=new JGraphHierarchicalLayout();
                }
                layout.run(facade);
                Map nested = facade.createNestedMap(true, true);

                graph.getGraphLayoutCache().edit(nested);
                graph.refresh();
                graph.validate();

            });

            controlPanel.add(layoutButton);

            add(controlPanel, BorderLayout.SOUTH);
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


    @Override
    public void update(Observable o, Object arg) {
        FilePublisher filePublisher = (FilePublisher) o;
        fileName=filePublisher.getFileName();
        cells=new ArrayList<>();
        start();
    }
}
