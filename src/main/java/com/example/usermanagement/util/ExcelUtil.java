package com.example.usermanagement.util;

import com.example.usermanagement.model.Usuario;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelUtil {

    // Espera arquivo .xlsx com colunas: nome | email | status (opcional)
    public static List<Usuario> lerUsuariosDoExcel(InputStream is) throws Exception {
        List<Usuario> usuarios = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            // Pular cabeçalho na primeira linha (presumimos que há cabeçalho)
            if (rows.hasNext()) rows.next();

            while (rows.hasNext()) {
                Row currentRow = rows.next();
                Usuario u = new Usuario();

                Cell cellNome = currentRow.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                Cell cellEmail = currentRow.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                Cell cellStatus = currentRow.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

                String nome = getCellString(cellNome);
                String email = getCellString(cellEmail);
                String status = getCellString(cellStatus);

                if (nome == null || nome.isBlank()) {
                    // Pular linha ou lançar erro no service
                    u.setNome("");
                } else {
                    u.setNome(nome.trim());
                }

                if (email == null) {
                    u.setEmail("");
                } else {
                    u.setEmail(email.trim());
                }

                if (status == null || status.isBlank()) {
                    u.setStatus("ATIVO");
                } else {
                    u.setStatus(status.trim().toUpperCase());
                }

                usuarios.add(u);
            }
        }
        return usuarios;
    }

    private static String getCellString(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            double d = cell.getNumericCellValue();
            return String.valueOf(d);
        } else if (cell.getCellType() == CellType.BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        } else {
            return cell.toString();
        }
    }
}
