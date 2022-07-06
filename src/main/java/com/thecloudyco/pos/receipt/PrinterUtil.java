package com.thecloudyco.pos.receipt;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;

public class PrinterUtil {
	
	public static DocFlavor flavor;
	public static PrintService service;
	public static PrintJobWatcher pjw;
  
  public static void finishPrint() throws PrintException
  {
	// send FF to eject the page
	    InputStream ff = new ByteArrayInputStream("\f".getBytes());
	    Doc docff = new SimpleDoc(ff, flavor, null);
	    DocPrintJob jobff = service.createPrintJob();
	    pjw = new PrintJobWatcher(jobff);
	    jobff.print(docff, null);
	    pjw.waitForDone();
  }
  
  
  public static void sendPrintJob(String info) throws PrintException, IOException {
	  
	  new Thread() {
		  public void run() {
			try {
				File printme = new File("PRINTME.CC");
				FileWriter w;
				w = new FileWriter("PRINTME.CC");
				
				w.write(info);
				  w.close();
				  
				  FileInputStream in = new FileInputStream(new File("PRINTME.CC"));
				  PrintService service = PrintServiceLookup.lookupDefaultPrintService();
				    PrintRequestAttributeSet  pras = new HashPrintRequestAttributeSet();
				    pras.add(new Copies(1));
				    DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
				    Doc doc = new SimpleDoc(in, flavor, null);

				    DocPrintJob job = service.createPrintJob();
				    PrintJobWatcher pjw = new PrintJobWatcher(job);
				    job.print(doc, pras);
				    pjw.waitForDone();
				    
				    printme.delete();
			} catch (IOException | PrintException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  }
	  }.run();
  }
}


class PrintJobWatcher {
  boolean done = false;

  PrintJobWatcher(DocPrintJob job) {
    job.addPrintJobListener(new PrintJobAdapter() {
      public void printJobCanceled(PrintJobEvent pje) {
        allDone();
      }
      public void printJobCompleted(PrintJobEvent pje) {
        allDone();
      }
      public void printJobFailed(PrintJobEvent pje) {
        allDone();
      }
      public void printJobNoMoreEvents(PrintJobEvent pje) {
        allDone();
      }
      void allDone() {
        synchronized (PrintJobWatcher.this) {
          done = true;
          //System.out.println("Printing done ...");
          PrintJobWatcher.this.notify();
        }
      }
    });
  }
  public synchronized void waitForDone() {
    try {
      while (!done) {
        wait();
      }
    } catch (InterruptedException e) {
    }
  }
}