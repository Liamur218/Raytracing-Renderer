package mesh;

public enum ModelFileType {
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

    @Override
    public String toString() {
        switch (this) {
            case STL_BIN -> {
                return "stl, binary";
            }
            case STL_ASCII -> {
                return "stl, ascii";
            }
            case WAVEFRONT_OBJ -> {
                return "wavefront obj";
            }
            default -> {
                return null;
            }
        }
    }
}
