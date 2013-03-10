package com.pannous.util;

import java.io.*;

import java.io.ByteArrayOutputStream;

import javax.sound.sampled.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Copyright 2013 Pannous GmbH
 * <p/>
 * User: info@pannous.com / me
 * Date: 3/9/13
 * Time: 1:33 PM
 */
public class AudioCapture {
    //    static float sampleRate = 16000;// 44100
    //    static float sampleRate = 8000;
    static float sampleRate = 4096;// 2000;
    static int sampleSizeInBits = 8;
//    static int sampleSizeInBits = 16;//Essential for audio generation!!

    static byte[] buffer;
    static int channels = 1;
    static boolean signed = true;
    static boolean bigEndian = true;

    static DataLine.Info info;
    static AudioFormat format;
    static SourceDataLine lineOut;
    static TargetDataLine lineIn;
    static boolean stopped = false;
    static boolean running = false;
    static ByteArrayOutputStream out;
    private static int bufferSize;
    public static int[] spikes;
    private static AudioCanvas canvas;

    public static void main(String args[]) throws Exception {
        start();
    }

    public static void start() {
        try {
            initCanvas();
            initLines();
            record();
//            doPlaySounds();
//            doPlaySounds();
//            playback();
        } catch (LineUnavailableException e) {
            System.err.println(e);
        }
    }

    private static void initCanvas() {
        spikes = new int[bufferSize];
        canvas = new AudioCanvas();
    }


    private static void initLines() throws LineUnavailableException {
        format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
        if (lineIn == null || !lineIn.isActive() || !lineIn.isOpen() || !lineIn.isRunning()) {
            lineIn = AudioSystem.getTargetDataLine(format);
            lineIn.open(format);
            lineIn.start();
        }
        if (lineOut == null || !lineIn.isRunning() || !lineIn.isOpen() || !lineIn.isActive()) {
            lineOut = AudioSystem.getSourceDataLine(format);
            lineOut.open(format);
            lineOut.start();
        }
        out = new ByteArrayOutputStream();
        bufferSize = (int) format.getSampleRate() * format.getFrameSize();
        buffer= new byte[bufferSize];
    }


    private static void record() throws LineUnavailableException {
        if (running) return;

        Runnable runner = new Runnable() {
            public void run() {
                try {
                    while (!stopped) {
                        running = true;
//                        Arrays.fill(buffer, (byte) 0);
                        int count = lineIn.read(buffer, 0, buffer.length);
                        if (count > 0) {
//                            int maximum = maximum(buffer);
//                            System.out.printf("max " + maximum);
//                            out.write(buffer, 0, count);
//                            int fftFrameSize=396;//bufferSize;//396;// bufferSize*2000;// 1000;
//                            int sign=-1;// -1 is FFT, 1 is IFFT;
//                            FFT fft = new FFT(fftFrameSize, sign);
//                            double[] double_buffer = new double[bufferSize];
//                            for (int i = 0; i < buffer.length; i++) double_buffer[i] = buffer[i];
//                            double_buffer=FFT.fft(double_buffer,null,false);
                            spikes= ByteFFT.fft(buffer, null, false);
//                            spikes = new int[bufferSize];
//                            for (int i = 0; i < buffer.length; i++) spikes[i] = (int)double_buffer[i];
                            canvas.repaint();
                            double minimum_threshold=40.0;//  arbitrary value
//                            int frequency= argMax(double_buffer,minimum_threshold);
                            int frequency= argMax(spikes,minimum_threshold);
                            if(frequency>100&&frequency<6000) {
                                if(frequency>2000)
                                java.awt.Toolkit.getDefaultToolkit().beep();
                                String x = "maximum frequency at " + frequency;
                                System.out.println(x);
                                canvas.frame.setTitle(x);
                            }
//                            byte audio[] = out.toByteArray();
//                            out.flush();
//                            lineOut.write(buffer, 0, count);
//                            lineOut.drain();
                        }
                    }
                    out.close();
                } catch (IOException e) {
                    System.err.println("I/O problems: " + e);
                    System.exit(-1);
                }
                running = false;
            }



        };
        Thread captureThread = new Thread(runner);
        captureThread.start();
    }

    private static int argMax(int[] spikes, double minimum_threshold) {
        double max = 0;
        int argMax = -1;
        for (int i = 200; i < spikes.length-500; i++) {//// skip dirty FFT ends!
            double v = spikes[i]+spikes[i+1]+spikes[i+2]+spikes[i+3]+spikes[i+4]+spikes[i+5]+spikes[i+6]+spikes[i+7];
            v=v/8;// ^^ average
            if (v > max &&  v>minimum_threshold) {
                max = v;
                argMax = i;
            }
        }
//        System.out.println("argMax at max "+max);
        return argMax;
    }


    private static void doPlaySounds() {
        Runnable runner = new Runnable() {
            @Override
            public void run() {
                playSounds();
            }
        };
        new Thread(runner).start();
    }

    private static void playSounds() {
        byte buffer[] = new byte[bufferSize];
        int j = 0;
        while (!stopped) {
            j += 1;
            double rand = (0.5 + Math.random()) * 10;
            double volume = Math.min(10.0 / 100.0, j * j * 0.2 / 100.0);
            System.out.println("rand " + rand);
            System.out.println("volume " + volume);
            for (int i = 0; i < bufferSize; i++) {
                int k = j * bufferSize + i;
                buffer[i] = (byte) (Math.sin(k / rand) * 255.0 * volume);//
            }
            running = true;
            lineOut.write(buffer, 0, bufferSize);
        }
    }

    private static int maximum(byte[] buffer) {
        int max = 0;
        for (int i = 0; i < bufferSize; i++) {
            max = Math.max(max, buffer[i]);
        }
        return max;
    }


    private static void playback(final byte[] audio) throws LineUnavailableException {

        Runnable runner = new Runnable() {
            int bufferSize = (int) format.getSampleRate() * format.getFrameSize();

            byte buffer[] = new byte[bufferSize];

            public void run() {
                try {
                    int count;
                    InputStream input = new ByteArrayInputStream(audio);
                    final AudioInputStream ais = new AudioInputStream(input, format, audio.length / format.getFrameSize());
                    while ((count = ais.read(buffer, 0, buffer.length)) != -1) {
                        if (count > 0) {
                            lineOut.write(buffer, 0, count);
                        }
                    }
                    lineOut.drain();
                    lineOut.close();
                } catch (IOException e) {
                    System.err.println("I/O problems: " + e);
                    System.exit(-3);
                }
            }
        };
        Thread playThread = new Thread(runner);
        playThread.start();
    }

    private static void playback() throws LineUnavailableException {
        Runnable runner = new Runnable() {
            int bufferSize = (int) format.getSampleRate() * format.getFrameSize();

            byte buffer[] = new byte[bufferSize];

            public void run() {
                try {
                    while ((true)) {
                        byte audio[] = out.toByteArray();
                        InputStream input = new ByteArrayInputStream(audio);
                        final AudioInputStream ais = new AudioInputStream(input, format, audio.length / format.getFrameSize());

                        int count;
                        while ((count = ais.read(buffer, 0, buffer.length)) != -1) {
                            if (count > 0) {
                                lineOut.write(buffer, 0, count);
                            }
                        }
                        lineOut.drain();
                        lineOut.close();
                    }
                } catch (IOException e) {
                    System.err.println("I/O problems: " + e);
                    System.exit(-3);
                }
            }
        };
        Thread playThread = new Thread(runner);
        playThread.start();

    }
}
