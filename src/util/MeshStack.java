package util;

import mesh.Mesh;

import java.util.*;

public class MeshStack extends ArrayList<Mesh> {

    int lastIndex = 0;

    public void push(Mesh element) {
        add(element);
        lastIndex++;
    }

    public Mesh removeFromEnd(Mesh mesh) {
        for (int i = lastIndex; i > 0; i--) {
            if (get(i).equals(mesh)) {
                remove(i);
                return mesh;
            }
        }
        return null;
    }
}
