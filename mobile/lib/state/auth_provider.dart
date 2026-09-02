import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

import '../models/auth_response.dart';
import '../models/usuario_logado.dart';
import '../services/api_config.dart';
import '../services/auth_api.dart';

const _chaveToken = 'token';
const _chaveNome = 'nome';
const _chaveEmail = 'email';

final secureStorageProvider = Provider<FlutterSecureStorage>((ref) {
  return const FlutterSecureStorage();
});

/// Token atual, lido de forma síncrona pelo interceptor do Dio a cada requisição.
final authTokenProvider = StateProvider<String?>((ref) => null);

final usuarioLogadoProvider = StateProvider<UsuarioLogado?>((ref) => null);

final dioProvider = Provider<Dio>((ref) {
  final dio = Dio(BaseOptions(baseUrl: apiBaseUrl));

  dio.interceptors.add(InterceptorsWrapper(
    onRequest: (options, handler) {
      final token = ref.read(authTokenProvider);
      if (token != null) {
        options.headers['Authorization'] = 'Bearer $token';
      }
      handler.next(options);
    },
    onError: (error, handler) {
      if (error.response?.statusCode == 401) {
        ref.read(authControllerProvider).logout();
      }
      handler.next(error);
    },
  ));

  return dio;
});

final authApiProvider = Provider<AuthApi>((ref) => AuthApi(ref.watch(dioProvider)));

/// Carrega uma sessão salva (se houver) assim que o app inicia.
final bootstrapProvider = FutureProvider<void>((ref) async {
  final storage = ref.read(secureStorageProvider);
  final token = await storage.read(key: _chaveToken);
  if (token == null) return;

  final nome = await storage.read(key: _chaveNome) ?? '';
  final email = await storage.read(key: _chaveEmail) ?? '';

  ref.read(authTokenProvider.notifier).state = token;
  ref.read(usuarioLogadoProvider.notifier).state = UsuarioLogado(nome: nome, email: email);
});

class AuthController {
  AuthController(this._ref);

  final Ref _ref;

  Future<void> registrar(String nome, String email, String senha) async {
    final resposta = await _ref.read(authApiProvider).registrar(nome, email, senha);
    await _persistir(resposta);
  }

  Future<void> login(String email, String senha) async {
    final resposta = await _ref.read(authApiProvider).login(email, senha);
    await _persistir(resposta);
  }

  Future<void> logout() async {
    final storage = _ref.read(secureStorageProvider);
    await storage.deleteAll();
    _ref.read(authTokenProvider.notifier).state = null;
    _ref.read(usuarioLogadoProvider.notifier).state = null;
  }

  Future<void> _persistir(AuthResponse resposta) async {
    final storage = _ref.read(secureStorageProvider);
    await storage.write(key: _chaveToken, value: resposta.token);
    await storage.write(key: _chaveNome, value: resposta.nome);
    await storage.write(key: _chaveEmail, value: resposta.email);

    _ref.read(authTokenProvider.notifier).state = resposta.token;
    _ref.read(usuarioLogadoProvider.notifier).state =
        UsuarioLogado(nome: resposta.nome, email: resposta.email);
  }
}

final authControllerProvider = Provider<AuthController>((ref) => AuthController(ref));
