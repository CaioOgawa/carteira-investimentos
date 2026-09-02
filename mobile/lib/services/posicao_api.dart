import 'package:dio/dio.dart';

import '../models/posicao.dart';

class PosicaoApi {
  final Dio _dio;

  PosicaoApi(this._dio);

  Future<List<Posicao>> listar() async {
    final response = await _dio.get('/posicoes');
    return (response.data as List)
        .map((json) => Posicao.fromJson(json as Map<String, dynamic>))
        .toList();
  }

  Future<Posicao> criar(Posicao posicao) async {
    final response = await _dio.post('/posicoes', data: posicao.toJson());
    return Posicao.fromJson(response.data as Map<String, dynamic>);
  }

  Future<Posicao> atualizar(int id, Posicao posicao) async {
    final response = await _dio.put('/posicoes/$id', data: posicao.toJson());
    return Posicao.fromJson(response.data as Map<String, dynamic>);
  }

  Future<void> remover(int id) async {
    await _dio.delete('/posicoes/$id');
  }
}
