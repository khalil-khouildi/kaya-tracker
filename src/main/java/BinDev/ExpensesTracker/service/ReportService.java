package BinDev.ExpensesTracker.service;

import BinDev.ExpensesTracker.entity.Expense;
import BinDev.ExpensesTracker.entity.User;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ExpenseService expenseService;
    private final IncomeService incomeService;

    // Générer un rapport PDF des dépenses
    public byte[] generateExpenseReport(User user, Integer year, Integer month) throws IOException {
        List<Expense> expenses = expenseService.getExpensesByMonth(user, year, month);
        BigDecimal totalExpenses = expenseService.getTotalExpensesByMonth(user, year, month);
        BigDecimal totalIncome = incomeService.getTotalIncomeByMonth(user, year, month);
        BigDecimal profitLoss = totalIncome.subtract(totalExpenses);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, out);

        document.open();

        // Titre
        Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
        Paragraph title = new Paragraph("Rapport des dépenses - " + month + "/" + year, titleFont);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(title);
        document.add(Chunk.NEWLINE);

        // Informations utilisateur
        Font infoFont = new Font(Font.HELVETICA, 12);
        document.add(new Paragraph("Utilisateur: " + user.getFirstName() + " " + user.getLastName(), infoFont));
        document.add(new Paragraph("Email: " + user.getEmail(), infoFont));
        document.add(Chunk.NEWLINE);

        // Résumé
        document.add(new Paragraph("Résumé financier:", new Font(Font.HELVETICA, 14, Font.BOLD)));
        document.add(new Paragraph("Total des revenus: " + totalIncome + " €", infoFont));
        document.add(new Paragraph("Total des dépenses: " + totalExpenses + " €", infoFont));
        document.add(new Paragraph("Profit/Perte: " + profitLoss + " €", infoFont));
        document.add(Chunk.NEWLINE);

        // Tableau des dépenses
        document.add(new Paragraph("Détail des dépenses:", new Font(Font.HELVETICA, 14, Font.BOLD)));
        document.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);

        // En-têtes
        String[] headers = {"Date", "Catégorie", "Description", "Montant (€)"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Paragraph(header, new Font(Font.HELVETICA, 12, Font.BOLD)));
            cell.setBackgroundColor(new java.awt.Color(200, 200, 200));
            table.addCell(cell);
        }

        // Données
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (Expense expense : expenses) {
            table.addCell(expense.getCreatedAt().format(formatter));
            table.addCell(expense.getCategory().getName().getDisplayName());
            table.addCell(expense.getDescription());
            table.addCell(expense.getAmount().toString() + " €");
        }

        document.add(table);

        // Total
        document.add(Chunk.NEWLINE);
        document.add(new Paragraph("Total des dépenses: " + totalExpenses + " €", new Font(Font.HELVETICA, 12, Font.BOLD)));

        document.close();

        return out.toByteArray();
    }

    // Générer un fichier CSV des dépenses
    public String generateExpenseCSV(User user, Integer year, Integer month) throws IOException {
        List<Expense> expenses = expenseService.getExpensesByMonth(user, year, month);
        BigDecimal totalExpenses = expenseService.getTotalExpensesByMonth(user, year, month);

        StringWriter stringWriter = new StringWriter();
        CSVPrinter csvPrinter = new CSVPrinter(stringWriter, CSVFormat.DEFAULT.withHeader(
                "Date", "Catégorie", "Description", "Montant (€)"
        ));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Expense expense : expenses) {
            csvPrinter.printRecord(
                    expense.getCreatedAt().format(formatter),
                    expense.getCategory().getName().getDisplayName(),
                    expense.getDescription(),
                    expense.getAmount().toString()
            );
        }

        csvPrinter.println();
        csvPrinter.printRecord("", "", "TOTAL:", totalExpenses.toString() + " €");

        csvPrinter.flush();
        return stringWriter.toString();
    }
}