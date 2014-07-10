------------------------------------------------------------------------------
 Release Notes for executing the MO-JGA implementation for the Bi-Objective 
 (Capacitated and Uncapacitated) Facility Location Problems.
 Version: 1.0
------------------------------------------------------------------------------
 Author:       Ronald Apriliyanto Halim 
               Transport and Logistics group
               Delft University of Technology
 URL:          http://www.tbm.tudelft.nl/en/about-faculty/departments/engineering-systems-and-services/tlo-section/staff/ronald-apriliyanto-halim/ronald-apriliyanto-halim/
 e-mail:       r.a.halim@tudelft.nl

--------------------------------------

0. What is MO-JGA?
   MO-JGA stands for Multi-Objective Java Genetic Algorithms. The purpose of MO-JGA is to 
   provide a framework for implementing applications based on multi-objective 
   evolutionary algorithms.
   
1.  Software Requirements

   For end users:
   * Java Virtual Machine (1.4 or later)
 
   This release was tested under Windows 2K. 
   It should run on any platform with a Sun compliant JVM.
   For more details about compatibility issues send a message to
   andres.medaglia@acm.org

2. Description

   This project illustrates the use of MO-JGA for solving a BiObjective Capacitated (and Uncapacitated) 
   Facility Location Problem (BOCFLP) using the Colombian National Coffee Growers Federation study case. 
   
      
   The project includes the following directories:
   
      /src       Directory including the project source files (.java files)
      /lib       Directoty including the required libraries (.jar files)
      /data/pFNC Directory including the data for the FNC case *
      /bin       Directory including the binary class files (.class files)

     
   
      
   Also, the project includes the following single files in the directory root:
   
      JGAConfigFNCUncapBin.ini   Configuration file for the uncapacitated problem using the binary representation
      JGAConfigFNCCapBin.ini     Configuration file for the capacitated problem using the binary representation

      BOCFLPSettingsFNC.ini      Settings file fpr the FNC case (Capacitated)
      BOUFLPSettingsFNC.ini      Settings file fpr the FNC case (UnCapacitated)
      
      *.bat                      Batch files for executing the GA application
   
   
3. Running the BOCFLP problem

   You have two options to run the BOCFLP implementation using the FNC�s data.
   
   1. Make a project (e.g., an Eclipse project) using the content of the BOCFLP directory (compressed file)
      and run it using the Eclipse menus.
      
      Include the following external jars in the project property "Java Build Path" (under Properties)
         lib/jga-20060602.jar
         lib/nsga-20060602.jar
         lib/ioutils-20051103.jar
         
      Before running, view the GA arguments in the configuration files JGAConfigFNCUncapBin.ini or JGAConfigFNCCapBin.ini
      
      Both configurations files are tied to the problem-specific configuration files 
      BOUFLPSettingsFNC.ini and BOCFLPSettingsFNC.ini respectively.
      
      
   2. Run the MO-GA for BOFLP using the batch files (.bat)

      You will find two batch files named mojgaFNCUncap.bat and mojgaFNCCap.bat
      
      mojgaFNCUncap.bat      Batch file for the FNC case (UnCapacitated)
      mojgaFNCCap.bat        Batch file for the FNC case (Capacitated)

      Click on the batch file from the Windows Explorer or execute it from the operating system shell.
       
      If you have problems, Open and review the path commands in the batch file.


4. Java Source Classes   

     The "src" directory contains the Java classes implemented for solving the BOFLP problem.
     
     The "main" function in BOFLPMain class must be invoked using the name of the JGA configuration file as argument. 
     
          For example:  java BOFLPMain JGAConfigFNCCapBin.ini 

     Additionally, a second argument can be used for defining the number of runs (replications) to be made. 
     
         For example:   java BOFLPMain JGAConfigFNCCapBin.ini 10


     When you run the BOFLPMain class without specifying number of runs, the application will run a single replicate
     and will show you the final population. Also, you can view a graphical plot using a map for the desired solution.

     When you run the BOFLPMain class specifying a number of runs, the application will show some statistics for each run.

     These outputs are implemented in user classes (src directory) by using services provided by MO-JGA.
 
     
5. Documentation

   The javadoc documentation of MO-JGA is found at  ..\doc\index.html 
   
   
6. Additional references

   Medaglia, A. L., Guti�rrez, E., and Villegas, J.G. Solving Facility Location Problems using a Tool for Rapid Development
   of Multi-Objective Evolutionary Algorithms (MOEAs). In Handbook of Research on Nature Inspired Computing for Economics 
   and Management. Jean-Phillipe Rennard (Ed.), 2006.
   
------------------------------------------------------------------------
First created: November 10th, 2004
Last updated : June 2nd, 2012
------------------------------------------------------------------------

   
   
