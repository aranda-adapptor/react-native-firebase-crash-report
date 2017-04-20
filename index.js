'use strict';

var { Platform, NativeModules } = require('react-native');
const _FirebaseCrashReport = NativeModules.RNFirebaseCrashReport;

export default class FirebaseCrashReport {

    static log(message: string) {
        _FirebaseCrashReport.log(message);
    }

    static logcat(message: string, level: number = null, tag: string = null) {
        if (Platform.OS === 'ios') {
            _FirebaseCrashReport.logcat(message);
        } else if (Platform.OS === 'android') {
            level = (typeof level !== 'number') ? 3 : level; // --- default level: 3 - DEBUG
            tag = (typeof tag !== 'string') ? 'RNFirebaseCrashReport' : tag;
            _FirebaseCrashReport.logcat(level, tag, message);
        }
    }

    static report(message) {
        _FirebaseCrashReport.report(message);
    }

    /**
     * Initialise the global error handler so that uncaught errors are automatically
     * reported to firebase.
     */
    static initErrorHandler() {
        const defaultGlobalHandler = global.ErrorUtils.getGlobalHandler();

        global.ErrorUtils.setGlobalHandler(async(error, isFatal) => {
            var stack = error.stack ? "STACK TRACE: " + error.stack : "";
            _FirebaseCrashReport.report(error.message + stack);

            // Call default handler in dev mode
            if (__DEV__ && defaultGlobalHandler) {
                defaultGlobalHandler(error, isFatal);
            }
        });
    }

    /**
     * Can be used to override console functions to send messages to
     * firebase crash reporting. Usage:
     *    console.info = FirebaseCrashReport.interceptLog(console.info);
     *    console.error = FirebaseCrashReport.interceptLog(console.error);
     */
    static interceptLog(originalFn) {
        return function() {
            const args = Array.prototype.slice.apply(arguments);
            let result = '';
            for (let i = 0; i < args.length; i++) {
                const arg = args[i];
                if (!arg || (typeof arg === 'string') || (typeof arg === 'number')) {
                    result += arg;
                }
                else {
                    result += JSON.stringify(arg);
                }
            }
            _FirebaseCrashReport.log(`console.${originalFn.name}: ${result}`);
            return originalFn.apply(console, arguments);
        };
    }
}
