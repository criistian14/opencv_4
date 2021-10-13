import 'dart:io';
import 'dart:typed_data';
import 'dart:ui';

import 'package:flutter/services.dart';
import 'package:flutter_cache_manager/flutter_cache_manager.dart';
import 'package:opencv_4/factory/pathfrom.dart';
import 'package:opencv_4/factory/utils.dart';

///Class for process [FindContours]
class FindContoursFactory {
  static const platform = const MethodChannel('opencv_4');

  static Future<List<Rect>?> findContours({
    required CVPathFrom pathFrom,
    required String pathString,
    required int mode,
    required int method,
    required Offset offset,
  }) async {
    File _file;
    Uint8List _fileAssets;

    List? result;
    switch (pathFrom) {
      case CVPathFrom.GALLERY_CAMERA:
        result = await platform.invokeMethod('findContours', {
          "pathType": 1,
          "pathString": pathString,
          "data": Uint8List(0),
          'mode': mode,
          'method': method,
          'offsetX': offset.dx,
          'offsetY': offset.dy,
        });
        break;
      case CVPathFrom.URL:
        _file = await DefaultCacheManager().getSingleFile(pathString);
        result = await platform.invokeMethod('findContours', {
          "pathType": 2,
          "pathString": '',
          "data": await _file.readAsBytes(),
          'mode': mode,
          'method': method,
          'offsetX': offset.dx,
          'offsetY': offset.dy,
        });

        break;
      case CVPathFrom.ASSETS:
        _fileAssets = await Utils.imgAssets2Uint8List(pathString);
        result = await platform.invokeMethod('findContours', {
          "pathType": 3,
          "pathString": '',
          "data": _fileAssets,
          'mode': mode,
          'method': method,
          'offsetX': offset.dx,
          'offsetY': offset.dy,
        });
        break;
    }

    if (result == null || result.isEmpty) return null;
    List<Rect> contours = [];

    result.forEach((e) {
      contours.add(Rect.fromLTWH(
        e["x"] as double,
        e["y"] as double,
        e["width"] as double,
        e["height"] as double,
      ));
    });

    return contours;
  }
}
