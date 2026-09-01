import 'package:dio/dio.dart';

import '../models/auth_response.dart';

class AuthApi {
  final Dio _dio;

  AuthApi(this._dio);

  Future<AuthResponse> registrar(String nome, String email, String senha) async {
    final response = await _dio.post('/auth/registro', data: {
      'nome': nome,
      'email': email,
      'senha': senha,
    });
    return AuthResponse.fromJson(response.data as Map<String, dynamic>);
  }

  Future<AuthResponse> login(String email, String senha) async {
    final response = await _dio.post('/auth/login', data: {
      'email': email,
      'senha': senha,
    });
    return AuthResponse.fromJson(response.data as Map<String, dynamic>);
  }
}
