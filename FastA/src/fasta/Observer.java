package fasta;

import java.util.ArrayList;
import java.util.Scanner;

public class Observer implements Runnable{
	private FastaStarter _fs;
	private ArrayList<String> commands = new ArrayList<String>();

	/**
	 * Observer constructor.
	 * @param sm	the FastaStarter.
	 */
	public Observer(FastaStarter fs) {
		this._fs=fs;
		this.loadCommands();
	}

	/**
	 * The run method.
	 */
	public void run() {
		boolean found = false;
		String[] line;
	    Scanner scanner = new Scanner(System.in);
        String userInput = "";
        while (!userInput.equals("stop")) {
        	int i;
        	found = false;
        	System.out.println("Enter a line:");
        	userInput = scanner.nextLine();
        	System.out.println("You entered: " + userInput);
        	line=userInput.split(", ", userInput.length());
        	for (i = 0; i < this.commands.size() && !found; i++) {
        		if (line[0].indexOf(this.commands.get(i)) == 0) {
        			found = true;
        		}
			}
        	if (found) {
        		switch (i) {
        		case 2:
        			this._fs.runLocalAlignments();
				}
        	} else if (!userInput.equals("exit")){	//Not found
        		System.out.println("wrong command");
        	}
        }
        this.stop();
        System.out.println("System has been terminated");
	}

	private void stop() {
		FastaStarter.stopAll = true;
	}


	private final void loadCommands() {
		this.commands.add("stop");
		this.commands.add("all2all");
	}

}
