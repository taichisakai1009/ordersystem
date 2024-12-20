//document.getElementById("QRcodeButton").addEventListener("click", function() {
//	fetch("/two-factor-auth/qr-code")
//		.then(response => response.blob())
//		.then(data => {
//			const imageElement = document.getElementById("qrCodeImage");
//			imageElement.src = URL.createObjectURL(data);
//			console.log("QRコード");
//		})
//		.catch(error => console.error('Error:', error));
//});