package ui;

import java.util.Scanner;

public class REPLTerminalView extends TerminalView {
    TerminalBuffer replBuffer;

    public REPLTerminalView() {
        super();

        TerminalViewRow row = createRow(100);
        TerminalViewCell cell = createCell(row, 100);

        resetLayout();

        replBuffer = cell.buffer();
    }

    private void printWelcome() {
        System.out.println("Welcome to the CS 240 Chess Game!");
        System.out.println(">>> Type Help to get started. <<<\n");
        System.out.flush();
    }

    @Override
    protected void eventLoop() {
        printWelcome();

        Scanner scanner = new Scanner(System.in);

        while (mounted) {
            System.out.print(">>> ");

            if (!scanner.hasNextLine()) break;

            String line = scanner.nextLine();
        }
    }
}
