package com.xshxy.seeklightbackend.util;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class DocumentParseUtil {

    /**
     * 解析pdf中的内容
     * @param file
     * @return
     * @throws IOException
     */
    public static String parsePdf(MultipartFile file) throws IOException {
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    /**
     * 解析word中的内容
     * @param file
     * @return
     * @throws Exception
     */
    public static String parseWord(MultipartFile file) throws Exception {

        XWPFDocument document = new XWPFDocument(file.getInputStream());

        StringBuilder text = new StringBuilder();

        for (XWPFParagraph paragraph : document.getParagraphs()) {
            text.append(paragraph.getText()).append("\n");
        }

        return text.toString();
    }

    /**
     * 解析excel的内容
     * @param file
     * @return
     * @throws Exception
     */

    public static String parseExcel(MultipartFile file) throws Exception {

        Workbook workbook = WorkbookFactory.create(file.getInputStream());

        StringBuilder text = new StringBuilder();

        for (Sheet sheet : workbook) {

            for (Row row : sheet) {

                for (Cell cell : row) {

                    text.append(cell.toString()).append(" ");
                }

                text.append("\n");
            }
        }

        return text.toString();
    }

    /**
     * 解析TXT内容
     * @param file
     * @return
     * @throws IOException
     */
    public static String parseTxt(MultipartFile file) throws IOException {

        return new String(file.getBytes(), StandardCharsets.UTF_8);
    }
}
