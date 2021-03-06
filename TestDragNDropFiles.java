import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.TooManyListenersException;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;


/**
 * @author www.markksantos.com
 *
 */
public class TestDragNDropFiles extends ResizeImage {

    /**
     * Static Main Method 
     * 
     * - Calls Constructor
     *
     */
    public static void main(String[] args) {
        new TestDragNDropFiles();
    }

    /**
     * Class Constructor
     * 
     * - Creates JFrame
     *
     */
    public TestDragNDropFiles() {
    	java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {}

                // Creates JFrame
                JFrame frame = new JFrame("Resizer"); 
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLayout(new BorderLayout());
                frame.add(new DropPane());
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.getContentPane().setBackground(Color.WHITE);
                frame.setVisible(true);

            }
        });
    }
    
    // JFrame Drag&Drop System + Creating Files + Opening PhotoShop
    public class DropPane extends JPanel {

    	private static final long serialVersionUID = 1L;
    	private DropTarget dropTarget;
    	private DropTargetHandler dropTargetHandler;
    	private boolean dragOver = false;
    	private JLabel message;
    	String filePath = new String();

    	public DropPane() {

    		setLayout(new GridBagLayout());
    		
    		// Text on middle of screen "Drop your images here/"
    		message = new JLabel();
    		message.setFont(message.getFont().deriveFont(Font.BOLD, 24));
    		add(message);
    		message.setText("Drop your images here.");

        }
    	
    	// Sets size of JFrame
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(400, 400);
        }

        protected DropTarget getMyDropTarget() {
            if (dropTarget == null) {
                dropTarget = new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, null);
            }
            return dropTarget;
        }

        protected DropTargetHandler getDropTargetHandler() {
            if (dropTargetHandler == null) {
                dropTargetHandler = new DropTargetHandler();
            }
            return dropTargetHandler;
        }

        @Override
        public void addNotify() {
            super.addNotify();
            try {
                getMyDropTarget().addDropTargetListener(getDropTargetHandler());
            } catch (TooManyListenersException ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void removeNotify() {
            super.removeNotify();
            getMyDropTarget().removeDropTargetListener(getDropTargetHandler());
        }

        // Makes JFrame Green 
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (dragOver) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(new Color(0, 255, 0, 64));
                g2d.fill(new Rectangle(getWidth(), getHeight()));
                g2d.dispose();
            }
        }

        // Deals with Files
        public void importFiles(final List<?> files) {
            Runnable run = new Runnable() {
                @Override
                public void run() {
                	int numImg = files.size();//amount of images being customized
                	for(int i=0;i<files.size();i++) {
                		System.out.println("Files: " + files.get(i));

                		String inputImagePath = files.get(i).toString(); //gets the path of image being resized
                		String outputImagePath = files.get(i).toString().substring(0, files.get(i).toString().lastIndexOf("/")) + "/icon_" + (i+1) + ".png"; //sets name of resized file
                		filePath = outputImagePath; // path to resized image

                		try {
                			// Sets image dimensions to 128x128
                			int scaledWidth = 128;
                			int scaledHeight = 128;
                			
                			// Calls 'resize' method from ResizeImage Class
                			ResizeImage.resize(inputImagePath, outputImagePath, scaledWidth, scaledHeight);

                		} catch (IOException ex) {
                			System.out.println("Error resizing the image.");
                			JOptionPane.showMessageDialog(null, "Error Resizing. Acceptable image extensions: \".png\", \".jpg\", \".jpeg\", and \".gif\"");
                			numImg = 0;
                		}

                	}

                    // Displays Alert with amount of images being customized.
                    JOptionPane pane = new JOptionPane("" + numImg + " images are being created. This may take a while.");
                    final JDialog dialog = pane.createDialog("Initiating Photoshop...");
                    dialog.setVisible(true);
                    

                    // Creates 'customized_action_script_photoshop.js' file
                    List<String> lines = Arrays.asList( 
                    		"(function (){\n" + 
                    		"\n" + 
                    		"    alert(\"PLEASE READ!\\nWelcome to the Custom MacOS File Generator.\\nA script is being executed right now and it might take a few minutes. Do not use Photoshop while this is running. Photoshop will automatizally close once the script is done running.\\nCreated by: www.markksantos.com\");\n" + 
                    		"\n" + 
                    		"for (var i = 0; i < " + numImg + " ; i++) {\n" + 
                    		"\n" + 
                    		"    app.preferences.rulerUnits = Units.PIXELS;\n" + 
                    		"    var activeDoc = app.activeDocument;\n" + 
                    		"\n" + 
                    		"    var replacementFile = new File(\"" + filePath.substring(0, filePath.toString().lastIndexOf("/")) + "/icon_\" + (i+1) + \".png\");\n" + 
                    		"\n" + 
                    		"    var iconLayer = activeDoc.artLayers.getByName(\"icon\");\n" + 
                    		"\n" + 
                    		"    function replaceContents (newFile) {  \n" + 
                    		"        var idLayer = stringIDToTypeID( \"placedLayerReplaceContents\" );  \n" + 
                    		"        var descript = new ActionDescriptor();  \n" + 
                    		"        var idnull = charIDToTypeID( \"null\" );  \n" + 
                    		"        descript.putPath( idnull, new File( newFile ) );  \n" + 
                    		"        var idPgNm = charIDToTypeID( \"PgNm\" );  \n" + 
                    		"        descript.putInteger( idPgNm, 1 );  \n" + 
                    		"        executeAction( idLayer, descript, DialogModes.NO );  \n" + 
                    		"        return app.activeDocument.activeLayer\n" + 
                    		"    }; \n" + 
                    		"\n" + 
                    		"    iconLayer = replaceContents(replacementFile);\n" + 
                    		"\n" + 
                    		"        opts = new ExportOptionsSaveForWeb();\n" + 
                    		"        opts.format = SaveDocumentType.PNG;\n" + 
                    		"        opts.PNG8 = false;\n" + 
                    		"        opts.quality = 100;\n" + 
                    		"\n" + 
                    		"        pngFile = new File(\"" + filePath.substring(0, filePath.toString().lastIndexOf("/")) + "/custom_folder_icon_\" + (i+1) + \".png\");\n" + 
                    		"        app.activeDocument.exportDocument(pngFile, ExportType.SAVEFORWEB, opts);\n" + 
                    		"    }\n" + "app.activeDocument.close(SaveOptions.DONOTSAVECHANGES);" +
                    		"\n" + "executeAction(app.charIDToTypeID(\"quit\"), undefined, DialogModes.NO);"+ 
                    		"\n" +
                    		"})();");

                    // Creates 'customized_action_script_photoshop.js' file
                    Path file = Paths.get("customized_action_script_photoshop.js");
                    try {
                    	Files.write(file, lines,  StandardCharsets.UTF_8);
                    } catch (IOException e) {
                    	// TODO Auto-generated catch block
                    	e.printStackTrace();
                    }


                    
                    // Check if 'PSUserConfig.txt' already exists && check if it was moved correctly.
                    File f = new File(filePath.substring(0, filePath.indexOf("/",7)) + "/Library/Preferences/Adobe Photoshop 2020 Settings/PSUserConfig.txt");
                    if(f.exists() && !f.isDirectory()) { 
                    	System.out.println("\"PSUserConfig.txt\" already exists!");
                    	
                    } else {
                    	
                        // Creates 'PSUserConfig.txt' File
                        List<String> lines2 = Arrays.asList("WarnRunningScripts 0");
                        Path file2 = Paths.get("PSUserConfig.txt");
                        try {
                        	Files.write(file2, lines2,  StandardCharsets.UTF_8);
                        } catch (IOException e) {
                        	e.printStackTrace();
                        }

                        
                        // Moves 'PSUserConfig.txt' to PhotoShop Setting Folder
                        Path temp = null;
                        try {
                        	temp = Files.move (file2,  
                        			Paths.get(filePath.substring(0, filePath.indexOf("/",7)) + "/Library/Preferences/Adobe Photoshop 2020 Settings/PSUserConfig.txt"));
                        } catch (IOException e2) { //error 
                        } 
                        
	                    if(temp != null) { System.out.println("\"PSUserConfig.txt\" moved successfully"); } 
	                    else{ System.out.println("--ERROR:  FAILED to move the \"PSUserConfig.txt\""); } 
                    }


                    // Copy 'custom.psd' to Desktop
                    InputStream is = TestDragNDropFiles.class.getResourceAsStream("/custom.psd");
                    try {
                    	Files.copy (is,  
                    			Paths.get(filePath.substring(0, filePath.indexOf("/",7)) + "/Desktop/custom.psd"));
                    } catch (IOException e2) {//error
                    	System.out.println("ERROR");
                    }  
                    
                    // Opens PhotoShop 'custom.psd' file from Desktop
                    try { 
                    	  System.getProperty("file.separator");
                    	  Runtime.getRuntime().exec(new String[] {"open", filePath.substring(0, filePath.indexOf("/",7)) + "/Desktop/custom.psd", "-a", "Adobe Photoshop 2020"});
                    	  System.out.println("Photoshop custom.psd opened Successfully!");

                   	  } catch (IOException e1) { 
                   		  System.out.println("Photoshop Was Not Opened!");
                   		  e1.printStackTrace(); 
                   	  }
                    

                    //opens "customized_action_script_photoshop.js" with PhotoShop
                    try {
                    	Runtime.getRuntime().exec(new String[] {"open", "customized_action_script_photoshop.js", "-a", "Adobe Photoshop 2020"});
                    	System.out.println("Script runned Successfully!");
                    } catch (IOException e) {
                    	System.out.println("Script Failed!");
                    	e.printStackTrace();
                    }

                }
            };
            SwingUtilities.invokeLater(run);
        }
        
        // Check if mouse is over the JFrame with files
        protected class DropTargetHandler implements DropTargetListener {

            protected void processDrag(DropTargetDragEvent dtde) {
                if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    dtde.acceptDrag(DnDConstants.ACTION_COPY);
                } else {
                    dtde.rejectDrag();
                }
            }

            @Override
            public void dragEnter(DropTargetDragEvent dtde) {
                processDrag(dtde);
                SwingUtilities.invokeLater(new DragUpdate(true, dtde.getLocation()));
                repaint();
            }

            @Override
            public void dragOver(DropTargetDragEvent dtde) {
                processDrag(dtde);
                SwingUtilities.invokeLater(new DragUpdate(true, dtde.getLocation()));
                repaint();
            }

            @Override
            public void dropActionChanged(DropTargetDragEvent dtde) {
            }

            @Override
            public void dragExit(DropTargetEvent dte) {
                SwingUtilities.invokeLater(new DragUpdate(false, null));
                repaint();
            }

            @Override
            public void drop(DropTargetDropEvent dtde) {

                SwingUtilities.invokeLater(new DragUpdate(false, null));

                Transferable transferable = dtde.getTransferable();
                if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    dtde.acceptDrop(dtde.getDropAction());
                    try {
                        List<?> transferData = (List<?>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
                        if (transferData != null && transferData.size() > 0) {
                            importFiles(transferData);
                            dtde.dropComplete(true);
                        }


                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                } else {
                    dtde.rejectDrop();
                }
            }

        }
        
        public class DragUpdate implements Runnable {

            private boolean dragOver;

            public DragUpdate(boolean dragOver, Point dragPoint) {
                this.dragOver = dragOver;
            }

            @Override
            public void run() {
                DropPane.this.dragOver = dragOver;
                DropPane.this.repaint();

            }
        }

    }
} 