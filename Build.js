const started = Date.now();

const WebSocket = require('ws');

const ws = new WebSocket('ws://192.168.43.1:8081/');

ws.on('open', () => {
  console.log('WebSocket Open!');

  ws.send(
    JSON.stringify({
      namespace: 'system',
      type: 'subscribeToNamespace',
      payload: 'ONBOTJAVA',
    })
  );

  ws.send(
    JSON.stringify({
      namespace: 'ONBOTJAVA',
      payload: '',
      type: 'build:launch',
    })
  );
});

ws.on('close', () => {
  console.error('WebSocket Closed');
});

ws.on('error', err => {
  throw err;
});

ws.on('message', msg => {
  const parsed = JSON.parse(msg);

  switch (parsed.type) {
    case 'build:status': {
      const payload = JSON.parse(parsed.payload);

      switch (payload.status) {
        case 'PENDING': {
          console.log('Build Pending');

          break;
        }

        case 'RUNNING': {
          console.log('Build Running');

          break;
        }

        case 'SUCCESSFUL': {
          console.log(`Build Successful in ${Date.now() - started}ms`);

          process.exit(0);

          break;
        }
      }

      break;
    }
  }
});
