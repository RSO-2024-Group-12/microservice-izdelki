INSERT INTO izdelek (id, naziv, opis, cena, aktiven, datum_dodajanja, datum_spremembe) VALUES
(100, 'Pametni telefon X', 'Napreden pametni telefon z odličnim fotoaparatom', 699.99, TRUE, CURRENT_DATE, CURRENT_DATE),
(101, 'Brezžične slušalke Y', 'Slušalke z dolgo življenjsko dobo baterije in čistim zvokom', 149.99, TRUE, CURRENT_DATE, CURRENT_DATE);

INSERT INTO lastnost (id, id_izdelek, lastnost, vrednost) VALUES
(100, 100, 'Barva', 'Črna'),
(101, 100, 'Kapaciteta', '128GB'),
(102, 101, 'Barva', 'Bela'),
(103, 101, 'Baterija', '30 ur predvajanja');

INSERT INTO slika (id, id_izdelek, url) VALUES
(100, 100, 'https://example.com/slike/pametni-telefon-x.jpg'),
(101, 101, 'https://example.com/slike/slusalke-y.jpg');