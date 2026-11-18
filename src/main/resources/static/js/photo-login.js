// Clicking the profile photo 3 times within 1 second opens the login page.
(function () {
    const photo = document.querySelector('.photo');
    if (!photo) return;

    let clicks = [];
    photo.addEventListener('click', function () {
        const now = Date.now();
        clicks = clicks.filter(t => now - t < 1000);
        clicks.push(now);
        if (clicks.length >= 3) {
            clicks = [];
            window.location.href = '/login';
        }
    });
})();
