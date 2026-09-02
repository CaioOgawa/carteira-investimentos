import 'package:dio/dio.dart';

import '../models/resumo_carteira.dart';

class ResumoApi {
  final Dio _dio;

  ResumoApi(this._dio);

  Future<ResumoCarteira> buscar() async {
    final response = await _dio.get('/carteira/resumo');
    return ResumoCarteira.fromJson(response.data as Map<String, dynamic>);
  }
}
