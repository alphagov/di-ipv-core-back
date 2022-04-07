package uk.gov.di.ipv.core.library.fixtures;

import java.util.List;
import java.util.Map;

public interface TestFixtures {
    String EC_PRIVATE_KEY =
            "MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQgOXt0P05ZsQcK7eYusgIPsqZdaBCIJiW4imwUtnaAthWhRANCAAQT1nO46ipxVTilUH2umZPN7OPI49GU6Y8YkcqLxFKUgypUzGbYR2VJGM+QJXk0PI339EyYkt6tjgfS+RcOMQNO";
    String EC_PUBLIC_KEY =
            "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEE9ZzuOoqcVU4pVB9rpmTzezjyOPRlOmPGJHKi8RSlIMqVMxm2EdlSRjPkCV5NDyN9/RMmJLerY4H0vkXDjEDTg==";
    String EC_PUBLIC_JWK =
            "{\"kty\":\"EC\",\"crv\":\"P-256\",\"x\":\"E9ZzuOoqcVU4pVB9rpmTzezjyOPRlOmPGJHKi8RSlIM\",\"y\":\"KlTMZthHZUkYz5AleTQ8jff0TJiS3q2OB9L5Fw4xA04\"}";

    String RSA_PRIVATE_KEY =
            "MIIJRAIBADANBgkqhkiG9w0BAQEFAASCCS4wggkqAgEAAoICAQDLVxVnUp8WaAWUNDJ/9HcsX8mzqMBLZnNuzxYZJLTKzpn5dHjHkNMjOdmnlwe65Cao4XKVdLDmgYHAxd3Yvo2KYb2smcnjDwbLkDoiYayINkL7cBdEFvmGr8h0NMGNtSpHEAqiRJXCi1Zm3nngF1JE9OaVgO6PPGcKU0oDTpdv9fetOyAJSZmFSdJW07MrK0/cF2/zxUjmCrm2Vk60pcIHQ+ck6pFsGa4vVE2R5OfLhklbcjbLBIBPAMPIObiknxcYY0UpphhPCvq41NDZUdvUVULfehZuD5m70PinmXs42JwIIXdX4Zu+bJ4KYcadfOfPSdhfUsWpoq2u4SHf8ZfIvLlfTcnOroeFN/VI0UGbPOK4Ki+FtHi/loUOoBg09bP5qM51NR8/UjXxzmNHXEZTESKIsoFlZTUnmaGoJr7QJ0jSaLcfAWaW652HjsjZfD74mKplCnFGo0Zwok4+dYOAo4pdD9qDftomTGqhhaT2lD+lc50gqb//4H//ydYajwED9t92YwfLOFZbGq3J2OJ7YRnk4NJ1D7K7XFTlzA/n0ERChTsUpUQaIlriTOuwjZyCWhQ+Ww98sQ0xrmLT17EOj/94MH/M3L0AKAYKuKi/V7He6/i8enda2llh75qQYQl4/Q3l16OzSGQG5f4tRwzfROdDjbi0TNy5onUXuvgU/QIDAQABAoICAQCsXbt1BGJ62d6wzLZqJM7IvMH8G3Y19Dixm7W9xpHCwPNgtEyVzrxLxgQsvif9Ut06lzFMY8h4/RsCUDhIPO86eLQSFaM/aEN4V2AQOP/Jz0VkYpY2T8thUqz3ZKkV+JZH+t8owj641Oh+9uQVA2/nqDm2Tb7riGZIKGY6+2n/rF8xZ0c22D7c78DvfTEJzQM7LFroJzouVrUqTWsWUtRw2Cyd7IEtQ2+WCz5eB849hi206NJtsfkZ/yn3FobgdUNclvnP3k4I4uO5vhzzuyI/ka7IRXOyBGNrBC9j0wTTITrS4ZuK0WH2P5iQcGWupmzSGGTkGQQZUh8seQcAEIl6SbOcbwQF/qv+cjBrSKl8tdFr/7eyFfXUhC+qZiyU018HoltyjpHcw6f12m8Zout60GtMGg6y0Z0CuJCAa+7LQHRvziFoUrNNVWp3sNGN422TOIACUIND8FiZhiOSaNTC36ceo+54ZE7io14N6raTpWwdcm8XWVMxujHL7O2Lra7j49/0csTMdzf24GVK31kajYeMRkkeaTdTnbJiRH04aGAWEqbs5JXMuRWPE2TWf8g6K3dBUv40Fygr0eKyu1PCYSzENtFzYKhfKU8na2ZJU68FhBg7zgLhMHpcfYLl/+gMpygRvbrFR1SiroxYIGgVcHAkpPaHAz9fL62H38hdgQKCAQEA+Ykecjxq6Kw/4sHrDIIzcokNuzjCNZH3zfRIspKHCQOfqoUzXrY0v8HsIOnKsstUHgQMp9bunZSkL8hmCQptIl7WKMH/GbYXsNfmG6BuU10SJBFADyPdrPmXgooIznynt7ETadwbQD1cxOmVrjtsYD2XMHQZXHCw/CvQn/QvePZRZxrdy3kSyR4i1nBJNYZZQm5UyjYpoDXeormEtIXl/I4imDekwTN6AJeHZ7mxh/24yvplUYlp900AEy0RRQqM4X73OpH8bM+h1ZLXLKBm4V10RUse+MxvioxQk7g1ex1jqc04k2MB2TviPXXdw0uiOEV21BfyUAro/iFlftcZLQKCAQEA0JuajB/eSAlF8w/bxKue+wepC7cnaSbI/Z9n53/b/NYf1RNF+b5XQOnkI0pyZSCmb+zVizEu5pgry+URp6qaVrD47esDJlo963xF+1TiP2Z0ZQtzMDu40EV8JaaMlA3mLnt7tyryqPP1nmTiebCa0fBdnvq3w4Y0Xs5O7b+0azdAOJ6mt5scUfcY5ugLIxjraL//BnKwdA9qUaNqf2r7KAKgdipJI4ZgKGNnY13DwjDWbSHq6Ai1Z5rkHaB7QeB6ajj/ZCXSDLANsyCJkapDPMESHVRWfCJ+nj4g3tdAcZqET6CYcrDqMlkscygI0o/lNO/IXrREySbHFsogkNytEQKCAQEAnDZls/f0qXHjkI37GlqL4IDB8tmGYsjdS7ZIqFmoZVE6bCJ01S7VeNHqg3Q4a5N0NlIspgmcWVPLMQqQLcq0JVcfVGaVzz+6NwABUnwtdMyH5cJSyueWB4o8egD1oGZTDGCzGYssGBwR7keYZ3lV0C3ebvvPQJpfgY3gTbIs4dm5fgVIoe9KflL6Vin2+qX/TOIK/IfJqTzwAgiHdgd4wZEtQQNchYI3NxWlM58A73Q7cf4s3U1b4+/1Qwvsir8fEK9OEAGB95BH7I6/W3WS0jSR7Csp2XEJxr8uVjt0Z30vfgY2C7ZoWtjtObKGwJKhm/6IdCAFlmwuDaFUi4IWhQKCAQEApd9EmSzx41e0ThwLBKvuQu8JZK5i4QKdCMYKqZIKS1W7hALKPlYyLQSNid41beHzVcX82qvl/id7k6n2Stql1E7t8MhQ/dr9p1RulPUe3YjK/lmHYw/p2XmWyJ1Q5JzUrZs0eSXmQ5+Qaz0Os/JQeKRm3PXAzvDUjZoAOp2XiTUqlJraN95XO3l+TISv7l1vOiCIWQky82YahQWqtdxMDrlf+/WNqHi91v+LgwBYmv2YUriIf64FCHep8UDdITmsPPBLaseD6ODIU+mIWdIHmrRugfHAvv3yrkL6ghaoQGy7zlEFRxUTc6tiY8KumTcf6uLK8TroAwYZgi6AjI9b8QKCAQBPNYfZRvTMJirQuC4j6k0pGUBWBwdx05X3CPwUQtRBtMvkc+5YxKu7U6N4i59i0GaWxIxsNpwcTrJ6wZJEeig5qdD35J7XXugDMkWIjjTElky9qALJcBCpDRUWB2mIzE6H+DvJC6R8sQ2YhUM2KQM0LDOCgiVSJmIB81wyQlOGETwNNacOO2mMz5Qu16KR6h7377arhuQPZKn2q4O+9HkfWdDGtmOaceHmje3dPbkheo5e/3OhOeAIE1q5n2RKjlEenfHmakSDA6kYa/XseB6t61ipxZR7gi2sINB2liW3UwCCZjiE135gzAo0+G7URcH+CQAF0KPbFooWHLwesHwj";

    String RSA_PUBLIC_KEY =
            "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAy1cVZ1KfFmgFlDQyf/R3LF/Js6jAS2Zzbs8WGSS0ys6Z+XR4x5DTIznZp5cHuuQmqOFylXSw5oGBwMXd2L6NimG9rJnJ4w8Gy5A6ImGsiDZC+3AXRBb5hq/IdDTBjbUqRxAKokSVwotWZt554BdSRPTmlYDujzxnClNKA06Xb/X3rTsgCUmZhUnSVtOzKytP3Bdv88VI5gq5tlZOtKXCB0PnJOqRbBmuL1RNkeTny4ZJW3I2ywSATwDDyDm4pJ8XGGNFKaYYTwr6uNTQ2VHb1FVC33oWbg+Zu9D4p5l7ONicCCF3V+GbvmyeCmHGnXznz0nYX1LFqaKtruEh3/GXyLy5X03Jzq6HhTf1SNFBmzziuCovhbR4v5aFDqAYNPWz+ajOdTUfP1I18c5jR1xGUxEiiLKBZWU1J5mhqCa+0CdI0mi3HwFmluudh47I2Xw++JiqZQpxRqNGcKJOPnWDgKOKXQ/ag37aJkxqoYWk9pQ/pXOdIKm//+B//8nWGo8BA/bfdmMHyzhWWxqtydjie2EZ5ODSdQ+yu1xU5cwP59BEQoU7FKVEGiJa4kzrsI2cgloUPlsPfLENMa5i09exDo//eDB/zNy9ACgGCriov1ex3uv4vHp3WtpZYe+akGEJeP0N5dejs0hkBuX+LUcM30TnQ424tEzcuaJ1F7r4FP0CAwEAAQ==";

    String RSA_PUBLIC_CERT =
            "MIIFOjCCAyICCQDZiXCLK2ceNDANBgkqhkiG9w0BAQsFADBeMQswCQYDVQQGEwJVSzEPMA0GA1UECAwGTG9uZG9uMQ8wDQYDVQQHDAZMb25kb24xDDAKBgNVBAoMA0dEUzELMAkGA1UECwwCREkxEjAQBgNVBAMMCVRlc3QgY2VydDAgFw0yMjAzMzAxMzA4MzVaGA8yMTIyMDMwNjEzMDgzNVowXjELMAkGA1UEBhMCVUsxDzANBgNVBAgMBkxvbmRvbjEPMA0GA1UEBwwGTG9uZG9uMQwwCgYDVQQKDANHRFMxCzAJBgNVBAsMAkRJMRIwEAYDVQQDDAlUZXN0IGNlcnQwggIiMA0GCSqGSIb3DQEBAQUAA4ICDwAwggIKAoICAQDLVxVnUp8WaAWUNDJ/9HcsX8mzqMBLZnNuzxYZJLTKzpn5dHjHkNMjOdmnlwe65Cao4XKVdLDmgYHAxd3Yvo2KYb2smcnjDwbLkDoiYayINkL7cBdEFvmGr8h0NMGNtSpHEAqiRJXCi1Zm3nngF1JE9OaVgO6PPGcKU0oDTpdv9fetOyAJSZmFSdJW07MrK0/cF2/zxUjmCrm2Vk60pcIHQ+ck6pFsGa4vVE2R5OfLhklbcjbLBIBPAMPIObiknxcYY0UpphhPCvq41NDZUdvUVULfehZuD5m70PinmXs42JwIIXdX4Zu+bJ4KYcadfOfPSdhfUsWpoq2u4SHf8ZfIvLlfTcnOroeFN/VI0UGbPOK4Ki+FtHi/loUOoBg09bP5qM51NR8/UjXxzmNHXEZTESKIsoFlZTUnmaGoJr7QJ0jSaLcfAWaW652HjsjZfD74mKplCnFGo0Zwok4+dYOAo4pdD9qDftomTGqhhaT2lD+lc50gqb//4H//ydYajwED9t92YwfLOFZbGq3J2OJ7YRnk4NJ1D7K7XFTlzA/n0ERChTsUpUQaIlriTOuwjZyCWhQ+Ww98sQ0xrmLT17EOj/94MH/M3L0AKAYKuKi/V7He6/i8enda2llh75qQYQl4/Q3l16OzSGQG5f4tRwzfROdDjbi0TNy5onUXuvgU/QIDAQABMA0GCSqGSIb3DQEBCwUAA4ICAQAXlxmRPcewD9NxAUoi6XIxz/ly1ealJognL8UyjT+gHopJ7VSk1gyrMZ0j0XNl/IqdiEgPwoJCKGWJWbzl5EB3/HIorkNEK+fQjz73cmpAoe9+B9qdU/zL+kRrQTfN0hfUxPYOpescGL9wErE6F9Ajcz5gC8x7u9Qza2QCzu50Z/51duVIavjKHCqmEh+gWZCUDBJAXGVh0z9XvFyDCm17bFNSzdmIkbELtz6xM9Qq1xwweD3mIJ4kKFoz2hKEyESVyDw9qS7CqCzdAwEHnVE1rXfeCYWUOK4MdCnbkM3ZbKwCUC880fPrqWK9hFg5wSQcTfmJzoKCTnI1nQnx7nGbrTbK44SerS2+zkvBApDGfE3K9fpkgvW5Z77f0GBt+4hC5TsWPURkiGFisoQ1+QRlmXaOpc2EiqNkBF6MGftT34Sw9SPf8q3zih5BSZN9PGBevXQMA3by5cS1y6e8mPnB/0xUvmEqcASS+JqmUZS4OuqcIrvtzdhPcftv3H5IjRA46foTAS45WgoIwTA0OBpBBOnb1XayRfpT+vIqIeI78gsDsQa+qcOLIVo4Al/ftodb657B9hLbWcSoJ39JUXmZ26DPE32vzCfhCLDGMLZRfZixEOHprPzzwXjIJ6wW2SFVDi+d7S3LYJ0bm3h/yaJ3TI6m9hVcIKWZSY0aSTkZeA==";

    String SIGNED_VC_1 =
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzI1NiJ9.eyJpc3MiOiJodHRwczpcL1wvaXNzdWVyLmV4YW1wbGUuY29tIiwic3ViIjoiaHR0cHM6XC9cL3N1YmplY3QuZXhhbXBsZS5jb20iLCJuYmYiOjE2NDczNjUxNDIsImV4cCI6MTY0NzM2NTQ0MiwidmMiOnsiQGNvbnRleHQiOlsiaHR0cHM6XC9cL3d3dy53My5vcmdcLzIwMThcL2NyZWRlbnRpYWxzXC92MSIsImh0dHBzOlwvXC92b2NhYi5sb25kb24uY2xvdWRhcHBzLmRpZ2l0YWxcL2NvbnRleHRzXC9pZGVudGl0eS12MS5qc29ubGQiXSwidHlwZSI6WyJWZXJpZmlhYmxlQ3JlZGVudGlhbCIsIklkZW50aXR5Q2hlY2tDcmVkZW50aWFsIl0sImNyZWRlbnRpYWxTdWJqZWN0Ijp7Im5hbWUiOlt7Im5hbWVQYXJ0cyI6W3sidHlwZSI6IkdpdmVuTmFtZSIsInZhbHVlIjoiQ2hyaXMifV19XSwiYmlydGhEYXRlIjpbeyJ2YWx1ZSI6IjE5ODQtMDktMjgifV0sImFkZHJlc3MiOlt7InBvc3RhbENvZGUiOiJMRTEyIDlCTiIsInR5cGUiOiJQb3N0YWxBZGRyZXNzIn1dfSwiZXZpZGVuY2UiOlt7InN0cmVuZ3RoIjo0LCJ0eXBlIjoiQ3JpU3R1YkNoZWNrIiwidmFsaWRpdHkiOjJ9XX19.1ebu-Biwynx0OjNdCOK1INjxYec-2CdWT26jthn7nJQjwB5EckGdalpJAjNGZUSvTqWXG2UteS-fb0oxV-5GMw";
    String SIGNED_VC_2 =
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzI1NiJ9.eyJpc3MiOiJodHRwczpcL1wvaXNzdWVyLmV4YW1wbGUuY29tIiwic3ViIjoiaHR0cHM6XC9cL3N1YmplY3QuZXhhbXBsZS5jb20iLCJuYmYiOjE2NDc0MzMzMDksImV4cCI6MTY0NzQzMzkwOSwidmMiOnsiQGNvbnRleHQiOlsiaHR0cHM6XC9cL3d3dy53My5vcmdcLzIwMThcL2NyZWRlbnRpYWxzXC92MSIsImh0dHBzOlwvXC92b2NhYi5sb25kb24uY2xvdWRhcHBzLmRpZ2l0YWxcL2NvbnRleHRzXC9pZGVudGl0eS12MS5qc29ubGQiXSwidHlwZSI6WyJWZXJpZmlhYmxlQ3JlZGVudGlhbCIsIklkZW50aXR5Q2hlY2tDcmVkZW50aWFsIl0sImNyZWRlbnRpYWxTdWJqZWN0Ijp7ImFkZHJlc3MiOlt7ImFkZHJlc3NSZWdpb24iOiJJbGxpbm9pcyIsInN0cmVldEFkZHJlc3MiOiIzNSBJZHN3b3J0aCBSb2FkIiwiYWRkcmVzc0xvY2FsaXR5IjoiU2hlZmZpZWxkIiwidHlwZSI6IlBvc3RhbEFkZHJlc3MiLCJhZGRyZXNzQ291bnRyeSI6IlVLIiwib3JnYW5pemF0aW9uTmFtZSI6IlNvZnR3YXJlIEx0ZCIsInBvc3RhbENvZGUiOiJTNSA2VU4ifV0sImJpcnRoRGF0ZSI6W3sidmFsdWUiOiIyMDIwLTAxLTAzIn1dLCJuYW1lIjpbeyJuYW1lUGFydHMiOlt7InR5cGUiOiJHaXZlbk5hbWUiLCJ2YWx1ZSI6IkFsaWNlIn0seyJ2YWxpZEZyb20iOiIyMDIwLTAzLTAxIiwidHlwZSI6IkZhbWlseU5hbWUiLCJ2YWx1ZSI6IkRvZSJ9XX1dfSwiZXZpZGVuY2UiOlt7IkdwZzQ1IjoiU2NvcmUifV19fQ.PUaeFsQPPvqu4cz0Pjof0a4v0lJCt6uCs7LwxVY2R7DIx31EX_2DBTK2FnsZcnb2HLXjHRsZZyWWh4M6dXnTuQ";

    Map<String, Object> CREDENTIAL_ATTRIBUTES_1 =
            Map.of(
                    "name",
                    List.of(
                            Map.of(
                                    "nameParts",
                                    List.of(
                                            Map.of("value", "Alice", "type", "GivenName"),
                                            Map.of("value", "Jane", "type", "GivenName"),
                                            Map.of("value", "Laura", "type", "GivenName"),
                                            Map.of(
                                                    "value",
                                                    "Doe",
                                                    "validFrom",
                                                    "2021-03-01",
                                                    "type",
                                                    "FamilyName"),
                                            Map.of(
                                                    "value",
                                                    "Musk",
                                                    "validFrom",
                                                    "2021-02-01",
                                                    "type",
                                                    "FamilyName")))),
                    "birthDate",
                    List.of(Map.of("value", "2020-01-03"), Map.of("value", "2021-01-03")),
                    "address",
                    List.of(
                            Map.of(
                                    "type", "PostalAddress",
                                    "organizationName", "Lebsack Inc",
                                    "streetAddress", "758 Huel Neck",
                                    "addressLocality", "Hagenesstad",
                                    "addressRegion", "Illinois",
                                    "postalCode", "38421-3292",
                                    "addressCountry", "Tonga"),
                            Map.of(
                                    "type", "PostalAddress",
                                    "postalCode", "M34 1AA")));

    Map<String, Object> CREDENTIAL_ATTRIBUTES_2 =
            Map.of(
                    "name",
                    List.of(
                            Map.of(
                                    "nameParts",
                                    List.of(
                                            Map.of("value", "Alice", "type", "GivenName"),
                                            Map.of(
                                                    "value",
                                                    "Doe",
                                                    "validFrom",
                                                    "2020-03-01",
                                                    "type",
                                                    "FamilyName")))),
                    "birthDate",
                    List.of(Map.of("value", "2020-01-03")),
                    "address",
                    List.of(
                            Map.of(
                                    "type", "PostalAddress",
                                    "organizationName", "Software Ltd",
                                    "streetAddress", "35 Idsworth Road",
                                    "addressLocality", "Sheffield",
                                    "addressRegion", "Illinois",
                                    "postalCode", "S5 6UN",
                                    "addressCountry", "UK")));

    Map<String, Object> CREDENTIAL_ATTRIBUTES_WITHOUT_SHARED_ATTRIBUTES =
            Map.of("name", Map.of("testProperty", "test value"));
}
