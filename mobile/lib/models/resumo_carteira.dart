class ResumoAtivo {
  final String ativo;
  final double quantidade;
  final double precoCompra;
  final double precoAtual;
  final double valorInvestido;
  final double valorAtual;
  final double ganhoPerda;
  final double percentual;

  const ResumoAtivo({
    required this.ativo,
    required this.quantidade,
    required this.precoCompra,
    required this.precoAtual,
    required this.valorInvestido,
    required this.valorAtual,
    required this.ganhoPerda,
    required this.percentual,
  });

  factory ResumoAtivo.fromJson(Map<String, dynamic> json) {
    return ResumoAtivo(
      ativo: json['ativo'] as String,
      quantidade: (json['quantidade'] as num).toDouble(),
      precoCompra: (json['precoCompra'] as num).toDouble(),
      precoAtual: (json['precoAtual'] as num).toDouble(),
      valorInvestido: (json['valorInvestido'] as num).toDouble(),
      valorAtual: (json['valorAtual'] as num).toDouble(),
      ganhoPerda: (json['ganhoPerda'] as num).toDouble(),
      percentual: (json['percentual'] as num).toDouble(),
    );
  }
}

class ResumoCarteira {
  final List<ResumoAtivo> ativos;
  final double valorTotalInvestido;
  final double valorTotalAtual;
  final double ganhoPerdaTotal;
  final double percentualTotal;

  const ResumoCarteira({
    required this.ativos,
    required this.valorTotalInvestido,
    required this.valorTotalAtual,
    required this.ganhoPerdaTotal,
    required this.percentualTotal,
  });

  factory ResumoCarteira.fromJson(Map<String, dynamic> json) {
    return ResumoCarteira(
      ativos: (json['ativos'] as List)
          .map((item) => ResumoAtivo.fromJson(item as Map<String, dynamic>))
          .toList(),
      valorTotalInvestido: (json['valorTotalInvestido'] as num).toDouble(),
      valorTotalAtual: (json['valorTotalAtual'] as num).toDouble(),
      ganhoPerdaTotal: (json['ganhoPerdaTotal'] as num).toDouble(),
      percentualTotal: (json['percentualTotal'] as num).toDouble(),
    );
  }
}
