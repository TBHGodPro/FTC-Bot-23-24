const started = Date.now();

const axios = require('axios');
const fs = require('fs/promises');
const { resolve } = require('path');

const dir = resolve(__dirname, 'org/firstinspires/ftc/teamcode');

(async () => {
  const { fileFromPath } = await import('./JSlib/fileFromPath.mjs');

  await axios
    .get('http://192.168.43.1:8080/', {
      timeout: 1250,
    })
    .catch(() => {
      throw new Error('Not Connected!');
    });

  await axios.post('http://192.168.43.1:8080/java/file/delete', 'delete=["src"]', {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    timeout: 1250,
  });

  async function checkDirectory(path) {
    const files = await fs.readdir(path);

    for (const file of files) {
      const newPath = resolve(path, file);

      if (file.endsWith('.java') || file.endsWith('.md')) {
        const form = new FormData();

        form.append('file', await fileFromPath(newPath));
        form.append('force', true);

        await axios.post('http://192.168.43.1:8080/java/file/upload', form, {
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
          },
          timeout: 1250,
        });
      } else await checkDirectory(newPath);
    }
  }
  await checkDirectory(dir);

  console.log(`Completed in ${Date.now() - started}ms!`);
})();
