# Katip
Bu projeyi geliştirme amacımız güvenli ve açık kaynak bir şifreli mesajlaşma uygulaması yapmaktır. Genel olarak yapmak istediklerimizi şu şekilde sıralayabiliriz:

1-) Kullanıcıların telefon numaralarını kullanmak yerine, kullanıcıların ürettiği bir eşsiz id üzerinden haberleşme sağlansın.

2-) Açık kaynak olması, kullanılan algoritmaların dökümanlara sahip olması ve güvenilir olarak bilindiği için şifreleme protokolü olarak Signal protokol kullanılsın.

3-) İstemciler arasında haberleşme RabbitMQ aracı yazılımı ile sağlansın. Bu yazılım, istemciler arasında haberleşme sağlamak için merkezi bir sunucu görevi görür. Başka bir istemciye gönderilmek istenen mesajlar veya şifreleme için kullanılacak açık anahtarlar bu yazılım aracılığıyla kuyruklanarak gönderilir. Yaygın kullanımı, güçlü bir dökümantasyon ve güvenilirliğe sahip olmasından dolayı bu yazılımı kullandık.
