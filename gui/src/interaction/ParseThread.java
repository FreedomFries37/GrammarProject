package interaction;


import structure.parse.ParseTree;

public class ParseThread implements Runnable {
    
    private Parser parser;
    private String str;
    private boolean completed;
    private ParseTree output;
    
    public ParseThread(Parser p, String parse){
        parser = p;
        str = parse;
        completed = false;
        output = null;
    }
    
    /**
     * When an object implementing interface <code>Runnable</code> is used to create a thread, starting the thread
     * causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        output = parser.parse(str);
        completed = true;
    }
    
    public double progress(){
        return parser.progress();
    }
    
    public boolean isCompleted() {
        return completed;
    }
    
    public ParseTree getOutput() {
        return output;
    }
}
