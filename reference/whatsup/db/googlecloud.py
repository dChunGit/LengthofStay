import os
from google.cloud import storage

bucket_name = "whats-up-255316.appspot.com"

def create_file(filename, ctype, file):
  print('Creating file %s for %s\n', filename)

  storage_client = storage.Client()
  bucket = storage_client.get_bucket(bucket_name)
  blob = bucket.blob(filename)

  blob.upload_from_string(file)

  print('Blob %s uploaded.', filename)


def read_file(filename):
  storage_client = storage.Client()
  bucket = storage_client.get_bucket(bucket_name)
  blob = bucket.blob(filename)

  content = blob.download_as_string().decode("ascii")

  print('Blob %s downloaded.', filename)
  return content


def delete_file(filename):
  storage_client = storage.Client()
  bucket = storage_client.get_bucket(bucket_name)
  blob = bucket.blob(filename)
  blob.delete()

  print('Blob {} deleted.'.format(filename))
