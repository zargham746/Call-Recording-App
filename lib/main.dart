// main.dart

import 'package:call_recording_app/audio_list.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() async {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Call Recorder',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: MyHomePage(),
      debugShowCheckedModeBanner: false,
    );
  }
}

class MyHomePage extends StatefulWidget {
  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  bool isRecording = false;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        centerTitle: true,
        title: const Text('Call Recorder'),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            ElevatedButton(
              onPressed: () {
                isRecording ? stopRecording() : startRecording();
              },
              child: Text(isRecording ? 'Stop Recording' : 'Start Recording'),
            ),
            ElevatedButton(
              onPressed: () {
                Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (context) => AudioList(),
                    ));
              },
              child: const Text("See Recorded Calls"),
            ),
          ],
        ),
      ),
    );
  }

  Future<void> startRecording() async {
    try {
      const channel = MethodChannel('call_recorder');
      final bool result = await channel.invokeMethod('startRecording');
      setState(() {
        isRecording = true;
      });
      print(result);
    } on PlatformException catch (e) {
      print("Failed to start recording: ${e.message}");
    }
  }

  Future<void> stopRecording() async {
    try {
      const channel = MethodChannel('call_recorder');
      final bool result = await channel.invokeMethod('stopRecording');
      setState(() {
        isRecording = false;
      });
      print(result);
    } on PlatformException catch (e) {
      print("Failed to stop recording: ${e.message}");
    }
  }
}
