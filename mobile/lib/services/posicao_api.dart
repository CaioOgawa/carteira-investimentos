import 'package:dio/dio.dart';

import '../models/posicao.dart';

/// URL base da API Spring Boot.
///
/// `localhost` funciona ao rodar o app no simulador iOS ou como app macOS.
/// Para rodar num dispositivo físico ou emulador Android, troque pelo IP da
/// máquina na rede local (ex: http://192.168.0.10:8080) ou pelo alias especial
/// do Android (10.0.2.2).
const _baseUrl = 'http://localhost:8080';

class PosicaoApi {
  final Dio _dio;

  PosicaoApi({Dio? dio})
      : _dio = dio ?? Dio(BaseOptions(baseUrl: _baseUrl));

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
