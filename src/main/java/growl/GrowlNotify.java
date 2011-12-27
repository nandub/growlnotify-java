package growl;

import java.io.PrintWriter;

import net.sf.libgrowl.Application;
import net.sf.libgrowl.GrowlConnector;
import net.sf.libgrowl.Notification;
import net.sf.libgrowl.NotificationType;
import net.sf.libgrowl.internal.IProtocol;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class GrowlNotify {
	public static final String EOL = System.getProperty("line.separator");
	public static final String HEADER = "growlnotify command-line.";
	public static final Options options;
	public static final CommandLineParser parser = new PosixParser();

	static {
		options = new Options();
		options.addOption("h", "help", false, "Display this help");
		options.addOption(
				"n",
				"name",
				true,
				"Set the name of the application that sends the notification [Default: growlnotify]");
		options.addOption("s", "sticky", false, "Make the notification sticky");
		options.addOption("I", "iconpath", true,
				"Specify a file whose icon will be the notification icon");
		options.addOption("m", "message", true, "Sets the message to be used");
		options.addOption("p", "priority", true,
				"Specify an int or named key (default is 0)");
		options.addOption("H", "host", true,
				"Specify a hostname or IP address to which to send a remote notification.");
		options.addOption("P", "port", true,
				"Specify a port to which to send a remote notification (default is 23053)");
		options.addOption(
				"t",
				"title",
				true,
				"Any text following will be treated as the title because that's the default argument behaviour");
	}

	// Main function executed
	public static void main(String[] args) throws Exception {
		GrowlNotify cli = new GrowlNotify();
		HelpFormatter help = new HelpFormatter();
		PrintWriter pw = new PrintWriter(System.out);
		int width = HelpFormatter.DEFAULT_WIDTH;
		if (args.length == 0) {
			cli.showUsage(pw, width, help);
			System.exit(1);
		}
		CommandLine line = null;
		try {
			line = parser.parse(options, args);
		} catch (ParseException ex) {
			cli.showUsage(pw, width, help);
			ex.printStackTrace();
			System.exit(1);
		}

		if (line.hasOption('h')) {
			help.printHelp(pw, width, "growlnotify", HEADER, options, 4, 2,
					null, true);
			pw.flush();
			System.exit(0);
		}

		String name = "growlnotify";
		boolean sticky = false;
		String iconPath = null;
		String message = "";
		int priority = 0;
		String host = "localhost";
		int port = IProtocol.DEFAULT_GROWL_PORT;
		String title = "";

		if (line.hasOption('n')) {
			name = line.getOptionValue('n');
		}
		if (line.hasOption('s')) {
			sticky = true;
		}
		if (line.hasOption('I')) {
			iconPath = line.getOptionValue('I');
		}
		if (line.hasOption('m')) {
			message = line.getOptionValue('m');
		}
		if (line.hasOption('p')) {
			priority = Integer.parseInt(line.getOptionValue('p'));
		}
		if (line.hasOption('H')) {
			host = line.getOptionValue('H');
		}
		if (line.hasOption('P')) {
			port = Integer.parseInt(line.getOptionValue('P'));
		}
		if (line.hasOption('t')) {
			title = line.getOptionValue('t');
		}

		final GrowlConnector growl = new GrowlConnector(host, port);
		final Application application = new Application(name);
		final NotificationType notificationType = new NotificationType(name,
				name, iconPath);
		final NotificationType[] notificationTypes = new NotificationType[] { notificationType };
		growl.register(application, notificationTypes);
		final Notification notification = new Notification(application,
				notificationType, title, message);
		notification.setPriority(priority);
		notification.setSticky(sticky);
		growl.notify(notification);
	}

	private void showUsage(PrintWriter pw, int width, HelpFormatter help) {
		help.printUsage(pw, width, "Try 'growlnotify --help' for more options.");
		pw.flush();
	}
}