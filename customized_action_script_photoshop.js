(function (){

    alert("PLEASE READ!\nWelcome to the Custom MacOS File Generator.\nA script is being executed right now and it might take a few minutes. Do not use Photoshop while this is running. Photoshop will automatizally close once the script is done running.\nCreated by: www.markksantos.com");

for (var i = 0; i < 1 ; i++) {

    app.preferences.rulerUnits = Units.PIXELS;
    var activeDoc = app.activeDocument;

    var replacementFile = new File("/Users/markksantos/Desktop/icons/icon_" + (i+1) + ".png");

    var iconLayer = activeDoc.artLayers.getByName("icon");

    function replaceContents (newFile) {  
        var idLayer = stringIDToTypeID( "placedLayerReplaceContents" );  
        var descript = new ActionDescriptor();  
        var idnull = charIDToTypeID( "null" );  
        descript.putPath( idnull, new File( newFile ) );  
        var idPgNm = charIDToTypeID( "PgNm" );  
        descript.putInteger( idPgNm, 1 );  
        executeAction( idLayer, descript, DialogModes.NO );  
        return app.activeDocument.activeLayer
    }; 

    iconLayer = replaceContents(replacementFile);

        opts = new ExportOptionsSaveForWeb();
        opts.format = SaveDocumentType.PNG;
        opts.PNG8 = false;
        opts.quality = 100;

        pngFile = new File("/Users/markksantos/Desktop/icons/custom_folder_icon_" + (i+1) + ".png");
        app.activeDocument.exportDocument(pngFile, ExportType.SAVEFORWEB, opts);
    }
app.activeDocument.close(SaveOptions.DONOTSAVECHANGES);
executeAction(app.charIDToTypeID("quit"), undefined, DialogModes.NO);
})();
