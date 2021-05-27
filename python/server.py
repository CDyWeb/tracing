from flask import Flask
from flask import request
import requests

from jaeger_client import Config
from flask_opentracing import FlaskTracer

app = Flask(__name__)

@app.route('/')
def pull_requests():

  # Fetch a list of pull requests on the opentracing repository
  github_url = "https://api.github.com/repos/opentracing/opentracing-python/pulls"
  r = requests.get(github_url)
  
  json = r.json()
  pull_request_titles = map(lambda item: item['title'], json)
  
  return 'OpenTracing Pull Requests: ' + ', '.join(pull_request_titles)


def initialize_tracer():
  config = Config(
      config={
          'sampler': {'type': 'const', 'param': 1}
      },
      service_name='hello-world')
  return config.initialize_tracer() # also sets opentracing.tracer


flask_tracer = FlaskTracer(initialize_tracer, True, app)

