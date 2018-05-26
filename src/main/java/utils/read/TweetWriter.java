package utils.read;

import lombok.AllArgsConstructor;
import lombok.Data;
import model.Tweet;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Data
public class TweetWriter {

    String folder;

    public void writeTargetTweetsToFile(List<Tweet> tweets) throws IOException {

        Workbook workbook = new XSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file
        String outputFileName = "TaggedTweets";
        Sheet sheet = workbook.createSheet(outputFileName);

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        // setting up header row
        Row headerRow = sheet.createRow(0);

        Cell cell = headerRow.createCell(0);
        cell.setCellValue("TAG");
        cell.setCellStyle(headerCellStyle);

        cell = headerRow.createCell(1);
        cell.setCellValue("Tweet");
        cell.setCellStyle(headerCellStyle);

        int rowNum = 1;
        for(Tweet tweet: tweets) {

            Row row = sheet.createRow(rowNum++);

            row.createCell(0)
                    .setCellValue(tweet.getTag());

            row.createCell(1)
                    .setCellValue(tweet.getContent());
        }

        for(int i = 0; i < 2; i++) {
            sheet.autoSizeColumn(i);
        }

        FileOutputStream fileOut = new FileOutputStream(folder+outputFileName+".xls");

        workbook.write(fileOut);
        fileOut.close();

        workbook.close();
    }

}
