package br.adv.cra.service;

public class TestRunner {
    public static void main(String[] args) {
        System.out.println("Test runner started");
        
        // Run the BasicTest manually
        BasicTest basicTest = new BasicTest();
        try {
            basicTest.testBasic();
            System.out.println("BasicTest passed successfully!");
        } catch (Exception e) {
            System.err.println("BasicTest failed: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("Test runner finished");
    }
}