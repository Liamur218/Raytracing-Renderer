package util;

public enum ModelType {
    STL_BIN,
    STL_ASCII,
    WAVEFRONT_OBJ;

    public String getExtension() {
        switch (this) {
            case STL_BIN, STL_ASCII -> {
                return "stl";
            }
            case WAVEFRONT_OBJ -> {
                return "obj";
            }
            default -> {
                return null;
            }
        }
    }
}
