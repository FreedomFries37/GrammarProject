package gui;

import structure.parse.ParseNode;
import structure.parse.ParseTree;

import javax.swing.*;
import java.awt.*;

public class TreeViewer extends JFrame{
    
    private class TreeNode extends JButton{
        private ParseNode base;
        
        TreeNode(ParseNode base){
            this.base = base;
            setText(base.getData());
            setSize(30, 15);
        }
        
        public Point topConnectPoint(){
            return new Point(getWidth()/2 + getX(), getY());
        }
        
        public Point bottomConnectPoint(){
            return new Point(getWidth()/2 + getX(), getHeight() + getY());
        }
    }
    
    private ParseTree tree;
    private MasterWindow masterWindow;
    private JPanel treeViewer;
    private JScrollPane scrollPane;
    
    /**
     * Creates a new, initially invisible <code>Frame</code> with the specified title.
     * <p>
     * This constructor sets the component's locale property to the value returned by <code>JComponent.getDefaultLocale</code>.
     *
     * @throws HeadlessException if GraphicsEnvironment.isHeadless() returns true.
     * @see GraphicsEnvironment#isHeadless
     * @see Component#setSize
     * @see Component#setVisible
     * @see JComponent#getDefaultLocale
     */
    public TreeViewer(ParseTree tree, MasterWindow masterWindow) throws HeadlessException {
        super("Tree View");
        this.tree = tree;
        this.masterWindow = masterWindow;
        treeViewer = new JPanel();
        treeViewer.setLayout(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(600, 600);
        populate();
        scrollPane = (JScrollPane) add(new JScrollPane(treeViewer));
        scrollPane.validate();
        setVisible(true);
    }
    
    public TreeViewer(ParseTree tree){
        this(tree, null);
    }
    
    public void populate(){
        treeViewer.setPreferredSize(new Dimension(1000, 1000));
        treeViewer.removeAll();
        
        
        validate();
    }
    
}
