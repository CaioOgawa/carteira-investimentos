class AuthResponse {
  final String token;
  final String nome;
  final String email;

  const AuthResponse({required this.token, required this.nome, required this.email});

  factory AuthResponse.fromJson(Map<String, dynamic> json) {
    return AuthResponse(
      token: json['token'] as String,
      nome: json['nome'] as String,
      email: json['email'] as String,
    );
  }
}
