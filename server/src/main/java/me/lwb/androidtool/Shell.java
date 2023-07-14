package me.lwb.androidtool;/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

import com.faendir.rhino_android.RhinoAndroidHelper;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrappedException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

import me.lwb.androidtool.android.FakeApp;
import me.lwb.androidtool.android.service.ClipboardManagerLike;
import me.lwb.androidtool.utils.Dump;

/**
 * The shell program.
 *
 * <p>Can execute scripts interactively or in batch mode at the command line. An example of
 * controlling the JavaScript engine.
 *
 * @author Norris Boyd
 */
public class Shell extends ScriptableObject {
    private static final long serialVersionUID = -5638074146250193112L;

    @Override
    public String getClassName() {
        return "global";
    }

    /**
     * Main entry point.
     *
     * <p>Process arguments as would a normal Java program. Also create a new Context and associate
     * it with the current thread. Then set up the execution environment and begin to execute
     * scripts.
     */
    public static void main(String args[]) {
        // Associate a new Context with this thread
        Context cx = new RhinoAndroidHelper().enterContext();
        try {
            // Initialize the standard objects (Object, Function, etc.)
            // This must be done before scripts can be executed.
            Shell shell = new Shell();
            cx.initStandardObjects(shell);

            // Define some global functions particular to the shell. Note
            // that these functions are not part of ECMA.
            String[] names = {"print", "quit", "version", "load", "help", "dump", "dumpClass"};
            shell.defineFunctionProperties(names, Shell.class, ScriptableObject.DONTENUM);
            shell.init();
            args = processOptions(cx, args);

            // Set up "arguments" in the global scope to contain the command
            // line arguments after the name of the script to execute
            Object[] array;
            if (args.length == 0) {
                array = new Object[0];
            } else {
                int length = args.length - 1;
                array = new Object[length];
                System.arraycopy(args, 1, array, 0, length);
            }
            Scriptable argsObj = cx.newArray(shell, array);
            shell.defineProperty("arguments", argsObj, ScriptableObject.DONTENUM);

            shell.processSource(cx, args.length == 0 ? null : args[0]);
        } finally {
            Context.exit();
        }
    }


    private void init() {

    }

    public static String dump(Object object) throws Exception {
        return Dump.INSTANCE.dump(object);
    }

    public static Object dumpClass() {
        try {
//            return new ClipboardManager(ServiceRegistry.INSTANCE.getClipboard()).getText();
           return new ClipboardManagerLike(FakeApp.PACKAGE_NAME, FakeApp.USER_ID).getPrimaryClip().getItemAt(0).getText();
//            FakeApp.init();
//            PackageManager packageManager =  FakeApp.getApplication().getPackageManager();
//
//            List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
//            for (PackageInfo installedPackage : installedPackages) {
//                System.out.println(packageManager.getDrawable(installedPackage.packageName,installedPackage.applicationInfo.icon,installedPackage.applicationInfo));
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "1";
    }

    /**
     * Parse arguments.
     */
    public static String[] processOptions(Context cx, String args[]) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (!arg.startsWith("-")) {
                String[] result = new String[args.length - i];
                for (int j = i; j < args.length; j++) result[j - i] = args[j];
                return result;
            }
            if (arg.equals("-version")) {
                if (++i == args.length) usage(arg);
                double d = Context.toNumber(args[i]);
                if (d != d) usage(arg);
                cx.setLanguageVersion((int) d);
                continue;
            }
            usage(arg);
        }
        return new String[0];
    }

    /**
     * Print a usage message.
     */
    private static void usage(String s) {
        p("Didn't understand \"" + s + "\".");
        p("Valid arguments are:");
        p("-version 100|110|120|130|140|150|160|170");
        System.exit(1);
    }

    /**
     * Print a help message.
     *
     * <p>This method is defined as a JavaScript function.
     */
    public void help() {
        p("");
        p("Command                Description");
        p("=======                ===========");
        p("help()                 Display usage and help messages. ");
        p("defineClass(className) Define an extension using the Java class");
        p("                       named with the string argument. ");
        p("                       Uses ScriptableObject.defineClass(). ");
        p("load(['foo.js', ...])  Load JavaScript source files named by ");
        p("                       string arguments. ");
        p("loadClass(className)   Load a class named by a string argument.");
        p("                       The class must be a script compiled to a");
        p("                       class file. ");
        p("print([expr ...])      Evaluate and print expressions. ");
        p("quit()                 Quit the shell. ");
        p("version([number])      Get or set the JavaScript version number.");
        p("");
    }

    /**
     * Print the string values of its arguments.
     *
     * <p>This method is defined as a JavaScript function. Note that its arguments are of the
     * "varargs" form, which allows it to handle an arbitrary number of arguments supplied to the
     * JavaScript function.
     */
    public static void print(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        for (int i = 0; i < args.length; i++) {
            if (i > 0) System.out.print(" ");

            // Convert the arbitrary JavaScript value into a string form.
            String s = Context.toString(args[i]);

            System.out.print(s);
        }
        System.out.println();
    }

    /**
     * Quit the shell.
     *
     * <p>This only affects the interactive mode.
     *
     * <p>This method is defined as a JavaScript function.
     */
    public void quit() {
        quitting = true;
    }

    /**
     * Get and set the language version.
     *
     * <p>This method is defined as a JavaScript function.
     */
    public static double version(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        double result = cx.getLanguageVersion();
        if (args.length > 0) {
            double d = Context.toNumber(args[0]);
            cx.setLanguageVersion((int) d);
        }
        return result;
    }

    /**
     * Load and execute a set of JavaScript source files.
     *
     * <p>This method is defined as a JavaScript function.
     */
    public static void load(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        Shell shell = (Shell) getTopLevelScope(thisObj);
        for (int i = 0; i < args.length; i++) {
            shell.processSource(cx, Context.toString(args[i]));
        }
    }

    /**
     * Evaluate JavaScript source.
     *
     * @param cx       the current context
     * @param filename the name of the file to compile, or null for interactive mode.
     */
    private void processSource(Context cx, String filename) {
        PrintStream stdout = System.out;
        PrintStream stderr = System.err;
        if (filename == null) {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String sourceName = "<stdin>";
            int lineno = 1;
            boolean hitEOF = false;
            do {
                int startline = lineno;
                stdout.flush();
                try {
                    String source = "";
                    // Collect lines of source to compile.
                    while (true) {
                        String newline;
                        newline = in.readLine();
                        if (newline == null) {
                            hitEOF = true;
                            break;
                        }
                        source = source + newline + "\n";
                        lineno++;
                        // Continue collecting as long as more lines
                        // are needed to complete the current
                        // statement.  stringIsCompilableUnit is also
                        // true if the source statement will result in
                        // any error other than one that might be
                        // resolved by appending more source.
                        if (cx.stringIsCompilableUnit(source)) break;
                    }
                    Object result = cx.evaluateString(this, source, sourceName, startline, null);
                    if (result != Context.getUndefinedValue()) {
                        stdout.println(Context.toString(result));
                    } else {
                        stdout.println();
                    }
                } catch (WrappedException we) {
                    stderr.println("" + we.getWrappedException().toString());
                } catch (EvaluatorException | JavaScriptException ee) {
                    // Some form of JavaScript error.
                    stderr.println(ee.toString());
                } catch (Exception ioe) {
                    stderr.println(ioe);
                }
                if (quitting) {
                    // The user executed the quit() function.
                    break;
                }
            } while (!hitEOF);
            stdout.println();
        } else {
            FileReader in = null;
            try {
                in = new FileReader(filename);
            } catch (FileNotFoundException ex) {
                Context.reportError("Couldn't open file \"" + filename + "\".");
                return;
            }

            try {
                // Here we evalute the entire contents of the file as
                // a script. Text is printed only if the print() function
                // is called.
                Object result = cx.evaluateReader(this, in, filename, 1, null);
                if (result != Context.getUndefinedValue()) {
                    stdout.println(Context.toString(result));
                }
            } catch (WrappedException we) {
                stderr.println(we.getWrappedException().toString());
            } catch (EvaluatorException | JavaScriptException ee) {
                stderr.println(ee.getMessage());
            } catch (Exception ioe) {
                stderr.println(ioe);
            } finally {
                try {
                    in.close();
                } catch (IOException ioe) {
                    stderr.println(ioe);
                }
            }
        }
    }

    private static void p(String s) {
        System.out.println(s);
    }

    private boolean quitting;
}