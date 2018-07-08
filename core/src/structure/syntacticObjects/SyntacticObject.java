package structure.syntacticObjects;

import structure.Reference;
import structure.parse.ParseNode;

public abstract class SyntacticObject {
    
    public abstract String getRepresentation();
    public abstract String getUpperRepresentation();
    public String getFullRepresentation(int maxLevels){
        return getFullRepresentation(maxLevels, 1);
    }
    protected abstract String getFullRepresentation(int maxLevels, int currentLevel);
    
    public abstract String generate();
    public String generate(int max_depth){
        return generate(max_depth-1, 0);
    }
    protected abstract String generate(int max_depth, int level);
    
    /**
     * Returns a string representation of the object. In general, the {@code toString} method returns a string that
     * "textually represents" this object. The result should be a concise but informative representation that is easy
     * for a person to read. It is recommended that all subclasses override this method.
     * <p>
     * The {@code toString} method for class {@code Object} returns a string consisting of the name of the class of
     * which the object is an instance, the at-sign character `{@code @}', and the unsigned hexadecimal representation
     * of the hash code of the object. In other words, this method returns a string equal to the value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return getRepresentation();
    }
}
