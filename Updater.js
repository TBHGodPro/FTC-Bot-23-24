const fs = require('fs');
const { resolve } = require('path');
const axios = require('axios');

fs.watch(
  resolve(__dirname, 'org/firstinspires/ftc/teamcode'),
  {
    recursive: true,
  },
  async (type, file) => {
    switch (type) {
      case 'change': {
        console.log(`Change detected in "${file}"`);

        const res = await axios
          .post(
            `http://192.168.43.1:8080/java/file/save?f=/src/org/firstinspires/ftc/teamcode/${file}`,
            {
              data: fs.readFileSync(resolve(__dirname, 'org/firstinspires/ftc/teamcode', file), 'utf-8'),
            },
            {
              headers: {
                'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
              },
              timeout: 1250,
            }
          )
          .then(res => res)
          .catch(err => (err.code || !err.response ? err : err.response));

        if (res instanceof Error) {
          if (res.code === 'ECONNABORTED') console.error('Not Connected!');
          else throw res;
        } else {
          if (res.status == 200) {
            console.log('Saved!');
          } else {
            console.error(res);
            process.exit(1);
          }
        }

        break;
      }

      case 'rename': {
        console.warn(`File "${file}" was renamed, this is not handled!`);

        break;
      }
    }
  }
);

console.log('Updater Ready!\n\n');
