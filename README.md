# AutoEntityCleaner

서버의 불필요한 엔티티를 주기적으로 자동 정리하여 서버 랙을 줄여주는 플러그인입니다.

## 주요 기능

- 설정된 간격에 따른 주기적인 엔티티 자동 정리
- 수동 정리를 위한 명령어 제공
- 정리할 엔티티 목록을 자유롭게 설정 가능
- 자동 정리 전 사용자에게 경고 메시지 방송 (On/Off 가능)
- 모든 사용자 메시지에 대해 한국어/영어 다국어 지원

## 설치 방법

1.  [AutoEntityCleaner-1.1.jar](https://github.com/boulmyong/AutoEntityCleaner/releases/download/v1.1/AutoEntityCleaner-1.1.jar) 파일을 Spigot 기반 마인크래프트 서버의 `plugins` 폴더에 넣습니다.
2.  서버를 실행하면 `plugins/AutoEntityCleaner` 폴더 안에 `config.yml` 파일이 자동으로 생성됩니다.
3.  서버의 특성에 맞게 `config.yml` 파일을 수정한 후, 서버를 리로드하거나 재시작합니다.

## 명령어 및 권한

| 명령어          | 설명                                       | 권한 노드 `autocleaner.clean` |
| --------------- | ------------------------------------------ | ----------------------------- |
| `/cleanentities`| 설정 파일에 지정된 모든 엔티티를 즉시 정리합니다. | `autocleaner.clean`           |

## 설정 (`config.yml`)

플러그인의 모든 기능은 `config.yml` 파일을 통해 설정할 수 있습니다.

```yaml
# 언어 설정 (0 = 영어, 1 = 한국어)
language: 1

# 엔티티 자동 정리 간격 (서버 틱 단위)
# 20틱 = 1초. 기본값 6000틱 = 5분.
# 자동 정리를 비활성화하려면 0으로 설정하세요.
clean-interval: 6000

# 정리할 엔티티 목록
# Spigot API에 명시된 유효한 엔티티 타입을 추가할 수 있습니다.
entities-to-clean:
  - ITEM
  - ARROW
  - EXPERIENCE_ORB

# 자동 정리 전 경고 메시지 사용 여부
warnings:
  enabled: true

# 플러그인에서 사용하는 모든 메시지 설정
# {time} 변수: 남은 시간을 표시합니다.
# {count} 변수: 정리된 엔티티 수를 표시합니다.
messages:
  manual_clean_start:
    en: "&aManual entity cleaning initiated..."
    ko: "&a수동 엔티티 정리를 시작합니다..."
  manual_clean_finish:
    en: "&aSuccessfully cleaned {count} entities."
    ko: "&a{count}개의 엔티티를 성공적으로 정리했습니다."
  no_permission:
    en: "&cYou do not have permission to use this command."
    ko: "&c이 명령어를 사용할 권한이 없습니다."
  warning:
    en: "&c[Warning] &fEntities will be cleaned in &e{time} &fseconds."
    ko: "&c[경고] &f엔티티가 &e{time}초&f 후에 정리됩니다."
```
