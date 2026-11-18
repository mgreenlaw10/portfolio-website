// On the edit pages, show a chosen image right away (before Publish).
document.querySelectorAll('input[type="file"]').forEach(function (input) {
    input.addEventListener('change', function () {
        const file = input.files[0];
        if (!file) return;
        const field = input.closest('.field');
        const current = field.querySelector('.preview');
        let img = current;
        if (current.tagName !== 'IMG') {
            // Replace the grey placeholder with an <img> using the same size/shape classes.
            img = document.createElement('img');
            img.className = current.className.replace('placeholder', '').trim();
            img.alt = 'New image';
            current.replaceWith(img);
        } else if (img.src.startsWith('blob:')) {
            URL.revokeObjectURL(img.src);
        }
        img.src = URL.createObjectURL(file);
    });
});
