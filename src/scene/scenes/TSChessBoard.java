package scene.scenes;

import mesh.*;
import scene.*;

import java.awt.*;

public abstract class TSChessBoard {
    public static Scene newScene() {
        Scene scene = new Scene();
        scene.setName("Chess Board");

        PolygonMesh mesh;
        String fileLocation = "assets/Large Models/chess/small";

        Vector camPos = new Vector(1, -2.5, 3);
        Vector camDir = new Vector(1, 2, -1.5);
        Vector camNorm = new Vector(0, 0, 1);
        scene.setCamera(new Camera(camPos, camDir, camNorm));

        float bishopScale = 0.8496f;
        float knightScale = 0.6672f;
        float rookScale = 0.5886f;
        float pawnScale = 0.55f;

        final Material whiteMaterial = new Material(Material.WHITE_MAT);
        final Material blackMaterial = new Material(Material.WHITE_MAT).setColor(new Color(162, 162, 162));

        // Board
        PolygonMesh whiteSquares = new PolygonMesh().setMaterial(whiteMaterial);
        PolygonMesh blackSquares = new PolygonMesh().setMaterial(blackMaterial);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                float x1 = i;
                float x2 = i + 1;
                float y1 = j;
                float y2 = j + 1;
                if ((i + j) % 2 == 0) {
                    whiteSquares.addQuad(x1, y1, 0, x2, y1, 0, x2, y2, 0, x1, y2, 0);
                } else {
                    blackSquares.addQuad(x1, y1, 0, x2, y1, 0, x2, y2, 0, x1, y2, 0);
                }
                if (i == j) {
                    float z = -0.125f;
                    if (i % 2 == 0) {
                        whiteSquares.addQuad(x1, 0, 0, x2, 0, 0, x2, 0, z, x1, 0, z);
                        whiteSquares.addQuad(0, y1, 0, 0, y2, 0, 0, y2, z, 0, y1, z);
                        blackSquares.addQuad(x1, 8, 0, x2, 8, 0, x2, 8, z, x1, 8, z);
                        blackSquares.addQuad(8, y1, 0, 8, y2, 0, 8, y2, z, 8, y1, z);
                    } else {
                        blackSquares.addQuad(x1, 0, 0, x2, 0, 0, x2, 0, z, x1, 0, z);
                        blackSquares.addQuad(0, y1, 0, 0, y2, 0, 0, y2, z, 0, y1, z);
                        whiteSquares.addQuad(x1, 8, 0, x2, 8, 0, x2, 8, z, x1, 8, z);
                        whiteSquares.addQuad(8, y1, 0, 8, y2, 0, 8, y2, z, 8, y1, z);
                    }
                }
            }
        }
        scene.addMesh(whiteSquares);
        scene.addMesh(blackSquares);

        mesh = new CubeMesh(4, 4, -(0.75 / 2 + 0.125), 9, 9, 0.75);
        scene.addMesh(mesh.setMaterial(new Material(Material.WHITE_MAT).setColor(new Color(150, 92, 0))));

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
        mesh.scale(bishopScale).setCenterAt(0.5, 2.5, bishopScale);
        scene.addMesh(mesh.setMaterial(whiteMaterial));
        mesh = ModelLoader.loadModel(fileLocation, "Bishop", ModelFileType.STL_BIN).normalize();
        mesh.scale(bishopScale).setCenterAt(0.5, 5.5, bishopScale);
        scene.addMesh(mesh.setMaterial(whiteMaterial));
        mesh = ModelLoader.loadModel(fileLocation, "Bishop", ModelFileType.STL_BIN).normalize();
        mesh.scale(bishopScale).rotate(0, 0, 180).setCenterAt(7.5, 2.5, bishopScale);
        scene.addMesh(mesh.setMaterial(blackMaterial));
        mesh = ModelLoader.loadModel(fileLocation, "Bishop", ModelFileType.STL_BIN).normalize();
        mesh.scale(bishopScale).rotate(0, 0, 180).setCenterAt(7.5, 5.5, bishopScale);
        scene.addMesh(mesh.setMaterial(blackMaterial));

        // Knights
        mesh = ModelLoader.loadModel(fileLocation, "Knight", ModelFileType.STL_BIN).normalize();
        mesh.scale(knightScale).rotate(0, 0, 90).setCenterAt(0.5, 1.5, knightScale);
        scene.addMesh(mesh.setMaterial(whiteMaterial));
        mesh = ModelLoader.loadModel(fileLocation, "Knight", ModelFileType.STL_BIN).normalize();
        mesh.scale(knightScale).rotate(0, 0, 90).setCenterAt(0.5, 6.5, knightScale);
        scene.addMesh(mesh.setMaterial(whiteMaterial));
        mesh = ModelLoader.loadModel(fileLocation, "Knight", ModelFileType.STL_BIN).normalize();
        mesh.scale(knightScale).rotate(0, 0, -90).setCenterAt(7.5, 1.5, knightScale);
        scene.addMesh(mesh.setMaterial(blackMaterial));
        mesh = ModelLoader.loadModel(fileLocation, "Knight", ModelFileType.STL_BIN).normalize();
        mesh.scale(knightScale).rotate(0, 0, -90).setCenterAt(7.5, 6.5, knightScale);
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
            scene.addMesh(mesh.setMaterial(blackMaterial));
        }

        return scene;
    }
}
