package chess;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

public class PieceLogicHelper {
    List<ChessMove> listOfMoves = new ArrayList<>(List.of());

    Collection<ChessMove> definePieceLogic(ChessBoard board, ChessPosition currentPosition) {
        int[][] royaltyMoves = {{1,1}, {-1, 1}, {-1, -1}, {1, -1}, {1, 0}, {0, 1}, {-1, 0}, {0, -1}};
        int[][] knightMoves = {{2, 1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}, {1, -2}, {2, -1}};
        int[][] bishopMoves = {{1,1}, {-1, 1}, {-1, -1}, {1, -1}};
        int[][] rookMoves = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
        int[][] pawnMoves = {{1, 0}, {1, -1}, {1, 1}};

        ChessPiece currentPiece = board.getPiece(currentPosition);


        if (currentPiece.getPieceType() == ChessPiece.PieceType.KING) {
            for (var dir : royaltyMoves) {
                int dRow = dir[0];
                int dCol = dir[1];
                directionalHelper(board, currentPosition, currentPosition, dRow, dCol, currentPiece.getTeamColor(), false);
            }
        }

        if (currentPiece.getPieceType() == ChessPiece.PieceType.QUEEN) {
            for (var dir : royaltyMoves) {
                int dRow = dir[0];
                int dCol = dir[1];
                directionalHelper(board, currentPosition, currentPosition, dRow, dCol, currentPiece.getTeamColor(), true);
            }
        }

        if (currentPiece.getPieceType() == ChessPiece.PieceType.BISHOP) {
            for (var dir : bishopMoves) {
                int dRow = dir[0];
                int dCol = dir[1];
                directionalHelper(board, currentPosition, currentPosition, dRow, dCol, currentPiece.getTeamColor(), true);
            }
        }

        if (currentPiece.getPieceType() == ChessPiece.PieceType.ROOK) {
            for (var dir : rookMoves) {
                int dRow = dir[0];
                int dCol = dir[1];
                directionalHelper(board, currentPosition, currentPosition, dRow, dCol, currentPiece.getTeamColor(), true);
            }
        }

        if (currentPiece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
            for (var dir : knightMoves) {
                int dRow = dir[0];
                int dCol = dir[1];
                directionalHelper(board, currentPosition, currentPosition, dRow, dCol, currentPiece.getTeamColor(), false);
            }
        }

        if (currentPiece.getPieceType() == ChessPiece.PieceType.PAWN) {
            for (var dir : pawnMoves) {
                int dRow = dir[0];
                int dCol = dir[1];
                pawnHelper(board, currentPosition, dRow, dCol, currentPiece.getTeamColor());
            }
        }

        return listOfMoves;
    }

    boolean isWithinBounds(ChessBoard board, ChessPosition position) {
        return (position.getRow() > 0 && position.getRow() <= board.gameBoard.length) && (position.getColumn() > 0 && position.getColumn() <= board.gameBoard.length);
    }

    int getDirection(ChessGame.TeamColor teamColor) {
        if (teamColor == ChessGame.TeamColor.WHITE) {
            return 1;
        }
        return -1;
    }

    boolean isStartingPiece(ChessPosition position, ChessGame.TeamColor teamColor) {
        if (position.getRow() == 2 && teamColor == ChessGame.TeamColor.WHITE) {
            return true;
        }
        if (position.getRow() == 7 && teamColor == ChessGame.TeamColor.BLACK) {
            return true;
        }

        return false;
    }

    void directionalHelper(ChessBoard board, ChessPosition currentPosition, ChessPosition nPosition, int dRow, int dCol, ChessGame.TeamColor currentTeamColor, boolean recurse) {
        int nextRow = nPosition.getRow() + dRow;
        int nextCol = nPosition.getColumn() + dCol;
        ChessPosition nextPosition = new ChessPosition(nextRow, nextCol);

        if (!isWithinBounds(board, nextPosition)) {
            return;
        }
        ChessPiece nextPositionPiece = board.getPiece(nextPosition);

        if (nextPositionPiece == null) {
            listOfMoves.add(new ChessMove(currentPosition, nextPosition, null));
            if (recurse) {
                directionalHelper(board, currentPosition, nextPosition, dRow, dCol, currentTeamColor, true);
            }
        } else if (nextPositionPiece.getTeamColor() != currentTeamColor) {
            listOfMoves.add(new ChessMove(currentPosition, nextPosition, null));
        }
    }

    void pawnHelper(ChessBoard board, ChessPosition currentPosition, int dRow, int dCol, ChessGame.TeamColor currentTeamColor) {
        int direction = getDirection(currentTeamColor);
        int nextRow = currentPosition.getRow() + dRow * direction;
        int nextCol = currentPosition.getColumn() + dCol * direction;

        ChessPosition nextPosition = new ChessPosition(nextRow, nextCol);
        if (!isWithinBounds(board, nextPosition)) {
            return;
        }
        ChessPiece nextPiece = board.getPiece(nextPosition);

        // check for forward motion
        if (dCol == 0) {
            if (nextPiece == null) {
                // Checks promotion
                if (promotion(currentPosition, nextPosition, currentTeamColor)) {
                    return;
                }
                // checks for starting piece
                if (isStartingPiece(currentPosition, currentTeamColor)) {
                    int nextNextRow = nextRow + direction;
                    ChessPosition nextNextPosition = new ChessPosition(nextNextRow, nextCol);
                    ChessPiece nextNextPiece = board.getPiece(nextNextPosition);
                    if (nextNextPiece == null) {
                        listOfMoves.add(new ChessMove(currentPosition, nextNextPosition, null));
                    }
                }
                listOfMoves.add(new ChessMove(currentPosition, nextPosition, null));
            }
        } else {
            if (nextPiece != null && nextPiece.getTeamColor() != currentTeamColor) {
                if (promotion(currentPosition, nextPosition, currentTeamColor)) {
                    return;
                }
                listOfMoves.add(new ChessMove(currentPosition, nextPosition, null));
            }
        }
    }

    boolean promotion(ChessPosition currentPosition, ChessPosition nextPosition, ChessGame.TeamColor teamColor) {
        List<ChessPiece.PieceType> promotionPieces = List.of(ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.ROOK, ChessPiece.PieceType.BISHOP);

        if ((nextPosition.getRow() == 8 && teamColor == ChessGame.TeamColor.WHITE) ||
                nextPosition.getRow() == 1 && teamColor == ChessGame.TeamColor.BLACK) {
            for (var piece : promotionPieces) {
                listOfMoves.add(new ChessMove(currentPosition, nextPosition, piece));
            }
            return true;
        }
        return false;
    }
}