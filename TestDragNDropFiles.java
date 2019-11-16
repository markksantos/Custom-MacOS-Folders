import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;
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
import java.util.concurrent.TimeUnit;
import java.util.Arrays;
import java.util.List;

/**
 * @author markksantos.com
 *
 */

public class TestDragNDropFiles extends ResizeImage {

    public static void main(String[] args) {
        new TestDragNDropFiles();
        
    }

    public TestDragNDropFiles() {
    	java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {}

                JFrame frame = new JFrame("Resizer");
               
                Image icon = Toolkit.getDefaultToolkit().getImage("icon.png");
                frame.setIconImage(icon);    

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

    public class DropPane extends JPanel {

    	private static final long serialVersionUID = 1L;
    	private DropTarget dropTarget;
    	private DropTargetHandler dropTargetHandler;
    	//private Point dragPoint;

    	private boolean dragOver = false;
    	//private BufferedImage target;


    	private JLabel message;
    	String filePath = new String();

    	public DropPane() {

    		// adds image to panel
    		//try {
    		//    target = ImageIO.read(new File("target.png"));  // targeted file
    		//} catch (IOException ex) {
    		//    ex.printStackTrace();
    		//}

    		setLayout(new GridBagLayout());
    		message = new JLabel();
    		message.setFont(message.getFont().deriveFont(Font.BOLD, 24));
    		add(message);
    		message.setText("Drop your images here.");

        }

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

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (dragOver) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(new Color(0, 255, 0, 64));
                g2d.fill(new Rectangle(getWidth(), getHeight()));
                /* Adds image when drag is over the panel
                 * 
                if (dragPoint != null && target != null) {
                    int x = dragPoint.x - 12;
                    int y = dragPoint.y - 12;
                    g2d.drawImage(target, x, y, this);
                }*/
                g2d.dispose();
            }
        }

        public void importFiles(final List<?> files) {
            Runnable run = new Runnable() {
                @Override
                public void run() {
                	int numImg = files.size();
                	for(int i=0;i<files.size();i++) {
                		System.out.println("Files: " + files.get(i));

                		String inputImagePath = files.get(i).toString(); //gets the path image being resized
                		String outputImagePath = files.get(i).toString().substring(0, files.get(i).toString().lastIndexOf("/")) + "/icon_" + (i+1) + ".png"; //sets name of resized file
                		filePath = outputImagePath;

                		try {
                			// resize to a fixed width
                			int scaledWidth = 128;
                			int scaledHeight = 128;
                			ResizeImage.resize(inputImagePath, outputImagePath, scaledWidth, scaledHeight);



                		} catch (IOException ex) {
                			System.out.println("Error resizing the image.");
                			JOptionPane.showMessageDialog(null, "Error Resizing. Acceptable image extensions: \".png\", \".jpg\", \".jpeg\", and \".gif\"");
                			//ex.printStackTrace();
                			numImg = 0;
                		}

                	}
                	
                    //JOptionPane.showMessageDialog(null, "" + numImg + " images are been created. This may take a while.");
                    
                    JOptionPane pane = new JOptionPane("" + numImg + " images are been created. This may take a while.");
                    final JDialog dialog = pane.createDialog("Initiating Photoshop...");
                    dialog.setVisible(true);

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
                    

                    Path file = Paths.get("customized_action_script_photoshop.js");
                    try {
                    	Files.write(file, lines,  StandardCharsets.UTF_8);
                    } catch (IOException e) {
                    	// TODO Auto-generated catch block
                    	e.printStackTrace();
                    }


                    // ~/Library/Preferences/Adobe Photoshop CC 2019 Settings/
                    List<String> lines2 = Arrays.asList("WarnRunningScripts 0");


                    Path file2 = Paths.get("PSUserConfig.txt");
                    try {
                    	Files.write(file2, lines2,  StandardCharsets.UTF_8);

                    } catch (IOException e) {
                    	// TODO Auto-generated catch block
                    	e.printStackTrace();
                    }

                    System.out.println(Paths.get("PSUserConfig.txt"));
                    System.out.println(Paths.get(filePath.substring(0, filePath.indexOf("/",7)) + "/Library/Preferences/Adobe Photoshop CC 2019 Settings/PSUserConfig.txt"));


                    Path temp = null;

                    // move PSUserConfig.txt to PhotoShop Setting Folder
                    try {
                    	temp = Files.move (file2,  
                    			Paths.get(filePath.substring(0, filePath.indexOf("/",7)) + "/Library/Preferences/Adobe Photoshop CC 2019 Settings/PSUserConfig.txt"));
                    } catch (IOException e2) { //error 
                    } 
                    if(temp != null) { System.out.println("File moved successfully"); } 
                    else{ System.out.println("Failed to move the file"); } 

                    // move custom.psd to desktop
                    Path file3 = Paths.get("custom.psd");
                    System.out.println(file3.toString());

                    try {
                    	temp = Files.move (file3,  
                    			Paths.get(filePath.substring(0, filePath.indexOf("/",7)) + "/Desktop/custom.psd"));
                    } catch (IOException e2) {//error
                    } 
					if(temp != null) { System.out.println("File moved successfully"); } 
					else{ System.out.println("Failed to move the file"); } 
                    
                    // Opens PhotoShop 'custom.psd' file and the 
                    try { 
                    	  System.getProperty("file.separator");
                    	  Runtime.getRuntime().exec(new String[] {"open", filePath.substring(0, filePath.indexOf("/",7)) + "/Desktop/custom.psd", "-a", "Adobe Photoshop CC 2019"});
                    	  System.out.println("Photoshop custom.psd opened Successfully!");
                         	
                    	  try {TimeUnit.SECONDS.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}

                   	  } catch (IOException e1) { 
                   		  System.out.println("Photoshop Was Not Opened!");
                   		  e1.printStackTrace(); 
                   	  }
                    // end of opening PhotoShop
                    

                    //opens the file with PhotoShop "customized_action_script_photoshop.js"
                    try {
                    	Runtime.getRuntime().exec(new String[] {"open", "customized_action_script_photoshop.js", "-a", "Adobe Photoshop CC 2019"});
                    	System.out.println("Script runned Successfully!");
                    } catch (IOException e) {
                    	System.out.println("Script Failed!");
                    	e.printStackTrace();
                    }

                    
                }
            };
            SwingUtilities.invokeLater(run);
        }

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
            	
         	    System.out.println("dtde: " + dtde);

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
            //private Point dragPoint;

            public DragUpdate(boolean dragOver, Point dragPoint) {
                this.dragOver = dragOver;
                //this.dragPoint = dragPoint;
            }

            @Override
            public void run() {
                DropPane.this.dragOver = dragOver;
                //DropPane.this.dragPoint = dragPoint;
                DropPane.this.repaint();
                
            }
        }

    }
}