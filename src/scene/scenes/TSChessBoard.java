package scene.scenes;

import mesh.*;
import scene.*;

import java.util.ArrayList;

public abstract class TSChessBoard {
    public static Scene newScene() {
        Scene scene = new Scene();
        scene.setName("Chess Board");

        PolygonMesh mesh;
        String fileLocation = "assets/big_assets/chess/small";

        Vector camPos = new Vector(1, -2.5, 2.75);
        Vector camDir = new Vector(1, 2, -1);
        Vector camNorm = new Vector(0, 0, 1);
        scene.setCamera(new Camera(camPos, camDir, camNorm));

        float bishopScale = 0.8496f;
        float knightScale = 0.6672f;
        float rookScale = 0.5886f;
        float pawnScale = 0.55f;

        final Material whiteMaterial = new Material(Material.WHITE_MAT);
        final Material blackMaterial = new Material(Material.DARK_GRAY_MAT);

        // Board
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                PolygonMesh polygonMesh = new PolygonMesh();
                polygonMesh.addQuad(
                        i, j, 0, i + 1, j, 0, i + 1, j + 1, 0, i, j + 1, 0);
                polygonMesh.setMaterial((i + j) % 2 == 0 ? whiteMaterial : blackMaterial);
                scene.addMesh(polygonMesh);
            }
        }

        // Kings
        mesh = ModelLoader.loadModel(fileLocation, "King", ModelFileType.STL_BIN).normalize();
        mesh.rotate(0, 0, 90).setCenterAt(0.5, 3.5, 1);
        scene.addMesh(mesh.setMaterial(whiteMaterial));
        mesh = ModelLoader.loadModel(fileLocation, "King", ModelFileType.STL_BIN).normalize();
        mesh.rotate(0, 0, -90).setCenterAt(7.5, 3.5, 1);
        scene.addMesh(mesh.setMaterial(blackMaterial));

        // Queens
        mesh = ModelLoader.loadModel(fileLocation, "Queen", ModelFileType.STL_BIN).normalize();
        mesh.rotate(0, 0, 90).setCenterAt(0.5, 4.5, 1);
        scene.addMesh(mesh.setMaterial(whiteMaterial));
        mesh = ModelLoader.loadModel(fileLocation, "Queen", ModelFileType.STL_BIN).normalize();
        mesh.rotate(0, 0, -90).setCenterAt(7.5, 4.5, 1);
        scene.addMesh(mesh.setMaterial(blackMaterial));

        // Bishops
        mesh = ModelLoader.loadModel(fileLocation, "Bishop", ModelFileType.STL_BIN).normalize();
        mesh.rotate(0, 0, 90).setCenterAt(0.5, 2.5, 1);
        scene.addMesh(mesh.setMaterial(whiteMaterial));
        mesh = ModelLoader.loadModel(fileLocation, "Bishop", ModelFileType.STL_BIN).normalize();
        mesh.rotate(0, 0, 90).setCenterAt(0.5, 5.5, 1);
        scene.addMesh(mesh.setMaterial(whiteMaterial));
        mesh = ModelLoader.loadModel(fileLocation, "Bishop", ModelFileType.STL_BIN).normalize();
        mesh.rotate(0, 0, -90).setCenterAt(7.5, 2.5, 1);
        scene.addMesh(mesh.setMaterial(blackMaterial));
        mesh = ModelLoader.loadModel(fileLocation, "Bishop", ModelFileType.STL_BIN).normalize();
        mesh.rotate(0, 0, -90).setCenterAt(7.5, 5.5, 1);
        scene.addMesh(mesh.setMaterial(blackMaterial));

        // Knights
        mesh = ModelLoader.loadModel(fileLocation, "Knight", ModelFileType.STL_BIN).normalize();
        mesh.rotate(0, 0, 90).setCenterAt(0.5, 1.5, 1);
        scene.addMesh(mesh.setMaterial(whiteMaterial));
        mesh = ModelLoader.loadModel(fileLocation, "Knight", ModelFileType.STL_BIN).normalize();
        mesh.rotate(0, 0, 90).setCenterAt(0.5, 6.5, 1);
        scene.addMesh(mesh.setMaterial(whiteMaterial));
        mesh = ModelLoader.loadModel(fileLocation, "Knight", ModelFileType.STL_BIN).normalize();
        mesh.rotate(0, 0, -90).setCenterAt(7.5, 1.5, 1);
        scene.addMesh(mesh.setMaterial(blackMaterial));
        mesh = ModelLoader.loadModel(fileLocation, "Knight", ModelFileType.STL_BIN).normalize();
        mesh.rotate(0, 0, -90).setCenterAt(7.5, 6.5, 1);
        scene.addMesh(mesh.setMaterial(blackMaterial));

        // Rooks
        mesh = ModelLoader.loadModel(fileLocation, "Rook", ModelFileType.STL_BIN).normalize();
        mesh.scale(rookScale).rotate(0, 0, 90).setCenterAt(0.5, 0.5, rookScale);
        scene.addMesh(mesh.setMaterial(whiteMaterial));
        mesh = ModelLoader.loadModel(fileLocation, "Rook", ModelFileType.STL_BIN).normalize();
        mesh.scale(rookScale).rotate(0, 0, 90).setCenterAt(0.5, 7.5, rookScale);
        scene.addMesh(mesh.setMaterial(whiteMaterial));
        mesh = ModelLoader.loadModel(fileLocation, "Rook", ModelFileType.STL_BIN).normalize();
        mesh.scale(rookScale).rotate(0, 0, -90).setCenterAt(7.5, 0.5, rookScale);
        scene.addMesh(mesh.setMaterial(blackMaterial));
        mesh = ModelLoader.loadModel(fileLocation, "Rook", ModelFileType.STL_BIN).normalize();
        mesh.scale(rookScale).rotate(0, 0, -90).setCenterAt(7.5, 7.5, rookScale);
        scene.addMesh(mesh.setMaterial(blackMaterial));

        // Pawns
        for (int i = 0; i < 8; i++) {
            mesh = ModelLoader.loadModel(fileLocation, "Pawn", ModelFileType.STL_BIN).normalize();
            mesh.scale(pawnScale).rotate(0, 0, 90).setCenterAt(1.5, 0.5 + i, pawnScale);
            scene.addMesh(mesh.setMaterial(whiteMaterial));
            mesh = ModelLoader.loadModel(fileLocation, "Pawn", ModelFileType.STL_BIN).normalize();
            mesh.scale(pawnScale).rotate(0, 0, -90).setCenterAt(6.5, 0.5 + i, pawnScale);
            scene.addMesh(mesh.setMaterial(Material.BLACK_MAT));
        }

        return scene;
    }
}
