class Posicao {
  final int? id;
  final String ativo;
  final double quantidade;
  final double precoCompra;
  final DateTime dataCompra;

  const Posicao({
    this.id,
    required this.ativo,
    required this.quantidade,
    required this.precoCompra,
    required this.dataCompra,
  });

  factory Posicao.fromJson(Map<String, dynamic> json) {
    return Posicao(
      id: json['id'] as int?,
      ativo: json['ativo'] as String,
      quantidade: (json['quantidade'] as num).toDouble(),
      precoCompra: (json['precoCompra'] as num).toDouble(),
      dataCompra: DateTime.parse(json['dataCompra'] as String),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'ativo': ativo,
      'quantidade': quantidade,
      'precoCompra': precoCompra,
      'dataCompra': dataCompra.toIso8601String().split('T').first,
    };
  }
}
