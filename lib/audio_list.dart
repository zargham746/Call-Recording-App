// ignore_for_file: library_private_types_in_public_api

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class AudioList extends StatefulWidget {
  const AudioList({super.key});

  @override
  _AudioListState createState() => _AudioListState();
}

class _AudioListState extends State<AudioList> {
  List<String> audioFiles = [];

  @override
  void initState() {
    super.initState();
    _getAudioFiles();
  }

  Future<void> _getAudioFiles() async {
    try {
      const platform = MethodChannel('call_recorder');
      final dynamic result = await platform.invokeMethod('getFiles');
      print('Raw result: $result');

      if (result is List<dynamic>) {
        List<String> audioFilesList = List<String>.from(result);

        setState(() {
          audioFiles = audioFilesList;
        });
      } else {
        print('Unexpected result type: ${result.runtimeType}');
      }
    } catch (e) {
      print('Error getting audio files: $e');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Audio Files List'),
      ),
      body: ListView.builder(
        itemCount: audioFiles.length,
        itemBuilder: (context, index) {
          return ListTile(
            title: Text(audioFiles[index]),
          );
        },
      ),
    );
  }
}
