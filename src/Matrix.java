import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Matrix {

    private FiniteField_F_2_n finiteField = null;

    private List<Packet> rows;

    public Matrix(List<Packet> rows, FiniteField_F_2_n finiteField) {
        this.rows = rows;
        this.finiteField = finiteField;
    }

    public void performGaussianElimination() {

        Logger logger = Logger.getLogger("log");

        for (int i = 0; i < rows.size(); i++) {

            // convert to leading non-zero
            convertToLeadingNonZero(i);

            // convert to leading one
            finiteField.multiplyPacketBy(rows.get(i), finiteField.complementByOne(rows.get(i).header[i]));

            // and other zeros
            for (int j = i+1; j < rows.size(); j++) {
                finiteField.addPackets(rows.get(j), finiteField.multiplyPacketBy(rows.get(i).clone(), rows.get(j).header[i]));
            }
        }

        /// Matrix is now upper triangular.
        logger.log(Level.INFO, String.format("Upper Triangular: %s", rows) );


        for (int i = rows.size() - 1; i >= 0; i--) {
            /// Starting with the last row in the matrix. Each row stored as a packet object.
            Packet packetToAdd = rows.get(i);
            /// For each of the preceding rows (w.r.t the current row) multiply each preceding row by its ith header
            //  element. Add the result to the preceding row.
            for (int j = i - 1; j >= 0; j--) {
                finiteField.addPackets(rows.get(j), finiteField.multiplyPacketBy(packetToAdd.clone(), rows.get(j).header[i]));
            }
        }

        logger.log(Level.INFO, String.format("Decoded: %s", rows));
    }

    public List<Packet> getOriginalPackets() {
        return rows;
    }

    private void convertToLeadingNonZero() {
        convertToLeadingNonZero(0);
    }

    /// convertToLeadingNonZero Rearranges the matrix so that the first row
    //  in the matrix has a non-zero value at row 1 col 1
    //  (numbers above refer to positions in the matrix as opposed to their corresponding array indices)
    private void convertToLeadingNonZero(int position) {
        if (position >= rows.size()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (rows.get(position).header[position] == 0) {
            for (int i = position; i < rows.size(); i++) {
                // Original code was not swapping the elements of the list properly.
                // Swapping failure was causing a whole lot of trouble as Gaussian elimination was not functioning properly.
                // Specifically in cases where random encoding vectors begin with a zero.

                if (rows.get(i).header[i] != 0) {
                    Collections.swap(rows, position, i);
                    break;
                }
            }
        }
    }
}
