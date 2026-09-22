#!/bin/bash
set -e

echo "Building Lambda deployment package with Docker (Python 3.11)..."

rm -rf build_package deployment.zip requirements-lambda.txt

cat requirements.txt | grep -v boto3 > requirements-lambda.txt

docker run --rm -v $(pwd):/var/task public.ecr.aws/sam/build-python3.11 sh -c "pip install -r /var/task/requirements-lambda.txt -t /var/task/build_package/ && chown -R $(id -u):$(id -g) /var/task/build_package/"

cp -r app build_package/

cd build_package
zip -r9q ../deployment.zip .
cd ..

rm -rf build_package requirements-lambda.txt
echo "Deployment package built: deployment.zip"
