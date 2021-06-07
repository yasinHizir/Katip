# Katip
Bu projeyi geliştirme amacımız güvenli ve açık kaynak bir şifreli mesajlaşma uygulaması yapmaktır. Genel olarak yapmak istediklerimizi şu şekilde sıralayabiliriz:

1-) Kullanıcıların telefon numaralarını kullanmak yerine, kullanıcıların ürettiği bir eşsiz id üzerinden haberleşme sağlansın.

2-) Şifreleme protokolü olarak Signal protokolü kullanılmıştır. Açık kaynak olması, kullanılan algoritmaların dökümanlara sahip olması ve güvenilir olarak bilinmesi bizim bu protokolü seçmemiz için önemli kriterler olmuştur.

3-) İstemciler arasında haberleşmeyi sağlamak amacı ile RabbitMQ aracı yazılımı kullanılmıştır. Bu yazılım istemciler arasındaki haberleşme için merkezi bir sunucu görevi görür. Başka bir istemciye gönderilmek istenen mesajlar veya şifreleme için kullanılacak açık anahtarlar bu yazılım aracılığıyla kuyruklanarak gönderilir. Yaygın kullanımı, güçlü bir dökümantasyon ve güvenilirliğe sahip olmasından dolayı bu yazılımı kullandık.
