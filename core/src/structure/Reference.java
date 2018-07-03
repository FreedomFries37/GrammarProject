package structure;

public class Reference<T> {
    
    T ref;
    
    public Reference(T data){
        ref = data;
    }
    
    public Reference() {
    }
    
    public T getRef() {
        return ref;
    }
    
    public void setRef(T ref) {
        this.ref = ref;
    }
}
