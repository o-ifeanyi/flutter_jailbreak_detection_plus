package yazan.alkamsheh.flutterjailbreakdetectionplus

import android.content.Context
import android.provider.Settings
import com.scottyab.rootbeer.RootBeer

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.StandardMethodCodec
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterPluginBinding



class FlutterJailbreakDetectionPlugin : FlutterPlugin, MethodCallHandler {
    private lateinit var context: Context
    private lateinit var channel: MethodChannel


    override fun onAttachedToEngine(binding: FlutterPluginBinding) {
        // RootBeer shells out (getprop, mount, which su) and queries
        // PackageManager for dozens of packages, which can block the main
        // thread for seconds on low-end devices. Handle calls in the background.
        channel = MethodChannel(
            binding.binaryMessenger,
            "flutter_jailbreak_detection",
            StandardMethodCodec.INSTANCE,
            binding.binaryMessenger.makeBackgroundTaskQueue(),
        )
        context = binding.applicationContext
        channel.setMethodCallHandler(this)
    }


    override fun onDetachedFromEngine(binding: FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }


    private fun isDevMode(): Boolean {
        return Settings.Secure.getInt(
            context.contentResolver,
            Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0
        ) != 0
    }


    override fun onMethodCall(call: MethodCall, result: Result): Unit {
        if (call.method.equals("jailbroken")) {
            val rootBeer = RootBeer(context)
            result.success(rootBeer.isRooted)
        } else if (call.method.equals("developerMode")) {
            result.success(isDevMode())
        } else {
            result.notImplemented()
        }
    }


}
