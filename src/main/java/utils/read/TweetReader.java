package utils.read;

import lombok.Data;
import model.Tweet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Data
public class TweetReader {

    int startPoint;
    int amount;
    String filePath;

    public TweetReader(String filePath, int startPoint, int amount) {

        this.filePath = filePath;
        this.startPoint = startPoint;
        this.amount = amount;

    }

    public List<Tweet> read() throws IOException {

        List<Tweet> tweets = new ArrayList<>();
        int counter = 0;
        FileInputStream inputStream = new FileInputStream(new File(filePath));

        Workbook workbook = new HSSFWorkbook(inputStream);
        Sheet firstSheet = workbook.getSheetAt(0);
        Iterator<Row> iterator = firstSheet.iterator();

        String tag = "";
        String content = "";

        for (int i = 0; i < startPoint; i++)
            iterator.next();

        while (iterator.hasNext() && (counter < amount)) {

            Row nextRow = iterator.next();
            Iterator<Cell> cellIterator = nextRow.cellIterator();

            while (cellIterator.hasNext()) {

                Cell nextCell = cellIterator.next();
                int columnIndex = nextCell.getColumnIndex();

                switch (columnIndex) {
                    case 0:
                        tag = nextCell.getStringCellValue();

                        break;
                    case 1:
                        content = nextCell.getStringCellValue();
                        break;
                }
            }

            Tweet tweet = new Tweet(tag, content);
            tweets.add(tweet);
            counter++;
        }

        workbook.close();
        inputStream.close();

        return tweets;
    }

}