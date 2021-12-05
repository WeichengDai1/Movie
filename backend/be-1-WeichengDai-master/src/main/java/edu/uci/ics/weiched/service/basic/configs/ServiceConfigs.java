package edu.uci.ics.weiched.service.basic.configs;

import edu.uci.ics.weiched.service.basic.logger.ServiceLogger;

public class ServiceConfigs {

    // TODO COMPLETE THIS CLASS

    public static final int MIN_SERVICE_PORT = 1024;
    public static final int MAX_SERVICE_PORT = 65535;

    // Default gateway configs
    private final String DEFAULT_SCHEME = "http://";
    private final String DEFAULT_HOSTNAME = "0.0.0.0";
    private final int    DEFAULT_PORT = 2942;
    private final String DEFAULT_PATH = "/activity";
    // Default logger configs
    private final String DEFAULT_OUTPUTDIR = "./logs/";
    private final String DEFAULT_OUTPUTFILE = "test.log";

    // Service configs
    private String scheme;
    private String hostName;
    private int port;

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    private String path;

    // Logger configs
    private String outputDir;
    private String outputFile;

    public ServiceConfigs() { }

    public ServiceConfigs(ConfigsModel cm) throws NullPointerException {
        if (cm == null) {
            ServiceLogger.LOGGER.severe("ConfigsModel not found.");
            throw new NullPointerException("ConfigsModel not found.");
        } else {
            // Set Service Configs
            // TODO
            scheme = cm.getServiceConfig().get("scheme");
            if(scheme == null){
                scheme = DEFAULT_SCHEME;
                System.err.println("Scheme not found, using default scheme.");
            }
            else {
                System.err.println("Scheme: "+ scheme);
            }

            hostName = cm.getServiceConfig().get("hostName");
            if(hostName == null){
                hostName = DEFAULT_HOSTNAME;
                System.err.println("hostName not found, using default hostname.");
            }
            else {
                System.err.println("Hostname: " + hostName);
            }

            port = Integer.parseInt(cm.getServiceConfig().get("port"));
            if(port == 0){
                port = DEFAULT_PORT;
                System.err.println("Port not found, using default port.");
            }else if(port<=MIN_SERVICE_PORT || port>= MAX_SERVICE_PORT){
                port = DEFAULT_PORT;
                System.err.println("Port not valid, using default port.");
            }else {
                System.err.println("Port: "+ port);
            }

            path = cm.getServiceConfig().get("path");
            if(path == null){
                path = DEFAULT_PATH;
                System.err.println("path not found, using default path");
            }else {
                System.err.println("Path: "+ path);
            }

            // Set Logger Configs
            // TODO
            outputDir = cm.getLoggerConfig().get("outputDir");
            if(outputDir == null){
                outputDir = DEFAULT_OUTPUTDIR;
                System.err.println("Output directory not found, using default output directory");
            }else {
                System.err.println("Outputdir: " + outputDir);
            }

            outputFile = cm.getLoggerConfig().get("outputFile");
            if(outputFile == null){
                outputFile = DEFAULT_OUTPUTFILE;
                System.err.println("Output file not found, using default output file");
            }else {
                System.err.println("Outputfile: "+outputFile);
            }
        }
    }

    public void currentConfigs() {
        // Log the current configs
        // TODO
        ServiceLogger.LOGGER.config("Scheme: "+ scheme);
        ServiceLogger.LOGGER.config("Hostname: " + hostName);
        ServiceLogger.LOGGER.config("Port: " + port);
        ServiceLogger.LOGGER.config("Path: " + path);
        ServiceLogger.LOGGER.config("Logger output directory: " + outputDir);
    }

    // Getters and Setters
    // TODO

 }